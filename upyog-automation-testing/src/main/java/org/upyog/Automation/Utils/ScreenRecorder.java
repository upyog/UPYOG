package org.upyog.Automation.Utils;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.model.Rational;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Screen recording utility for Selenium browser automation.
 *
 * <p>Supports both Headed (Local UI) and Headless browser execution modes.
 * Uses periodic frame capture via WebDriver TakesScreenshot API and encodes
 * standard MP4 video files using pure Java (JCodec), stored in temporary directory.</p>
 */
public class ScreenRecorder {

    private static final Logger logger = LoggerFactory.getLogger(ScreenRecorder.class);

    private static final int DEFAULT_FPS = 4;
    private static final int CAPTURE_INTERVAL_MS = 280;
    private static final int MAX_RECORDINGS_KEPT = 30;

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1, r -> {
                Thread t = new Thread(r, "screen-recorder-thread");
                t.setDaemon(true);
                return t;
            });

    private static ScheduledFuture<?> recordingTask;
    private static final List<File> capturedFrameFiles = Collections.synchronizedList(new ArrayList<>());
    private static File tempFramesDir = null;
    private static final AtomicBoolean isRecording = new AtomicBoolean(false);
    private static WebDriver currentDriver;
    private static String currentModule = "MODULE";
    private static String currentTestCase = "TEST";
    private static long recordingStartTime = 0;
    private static File lastRecordedFile = null;

    /**
     * Starts video recording of the current browser session.
     *
     * @param driver active WebDriver instance
     * @param moduleName name of the module
     * @param testCaseName name or ID of the test case
     */
    public static synchronized void startRecording(WebDriver driver, String moduleName, String testCaseName) {
        if (driver == null) {
            logger.warn("Cannot start screen recording: WebDriver is null");
            return;
        }

        // If a previous recording was still active, finalize it first
        if (isRecording.get()) {
            stopRecording();
        }

        currentDriver = driver;
        currentModule = moduleName != null && !moduleName.isBlank() ? moduleName : "MODULE";
        currentTestCase = testCaseName != null && !testCaseName.isBlank() ? testCaseName : "TEST";
        capturedFrameFiles.clear();

        try {
            tempFramesDir = Files.createTempDirectory("upyog_rec_").toFile();
        } catch (Exception e) {
            logger.warn("Could not create temp frames directory, using fallback temp dir: {}", e.getMessage());
            tempFramesDir = new File(System.getProperty("java.io.tmpdir"), "upyog_rec_" + System.currentTimeMillis());
            tempFramesDir.mkdirs();
        }

        isRecording.set(true);
        recordingStartTime = System.currentTimeMillis();

        // Capture initial frame immediately
        captureCurrentFrame();

        // Schedule periodic frame captures
        recordingTask = scheduler.scheduleWithFixedDelay(() -> {
            if (isRecording.get() && currentDriver != null) {
                captureCurrentFrame();
            }
        }, CAPTURE_INTERVAL_MS, CAPTURE_INTERVAL_MS, TimeUnit.MILLISECONDS);

        logger.info("🎬 Screen recording started for [{}_{}]", currentModule, currentTestCase);
    }

    /**
     * Manually records a single frame (e.g. at important step transitions or submissions).
     *
     * @param driver active WebDriver instance
     */
    public static void recordFrame(WebDriver driver) {
        if (!isRecording.get() || driver == null) {
            return;
        }
        try {
            captureCurrentFrame();
        } catch (Exception e) {
            logger.debug("Could not capture manual frame: {}", e.getMessage());
        }
    }

    /**
     * Captures the current browser state, saves it to a temp JPEG frame on disk,
     * freeing memory immediately to prevent JVM heap exhaustion.
     */
    private static void captureCurrentFrame() {
        if (currentDriver == null || tempFramesDir == null) return;

        try {
            byte[] screenshotBytes = ((TakesScreenshot) currentDriver).getScreenshotAs(OutputType.BYTES);
            if (screenshotBytes == null || screenshotBytes.length == 0) return;

            BufferedImage rawImage = ImageIO.read(new ByteArrayInputStream(screenshotBytes));
            if (rawImage == null) return;

            // Normalize image for video encoding (dimensions must be even numbers and RGB type)
            BufferedImage normalized = normalizeImage(rawImage);
            
            File frameFile = new File(tempFramesDir, String.format("frame_%06d.jpg", capturedFrameFiles.size()));
            ImageIO.write(normalized, "jpg", frameFile);
            capturedFrameFiles.add(frameFile);

        } catch (Exception e) {
            logger.debug("Frame capture skipped: {}", e.getMessage());
        }
    }

    /**
     * Normalizes a BufferedImage to ensure dimensions are even and pixel format is standard RGB.
     */
    private static BufferedImage normalizeImage(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();

        // Ensure even dimensions
        int targetW = (w % 2 == 0) ? w : w - 1;
        int targetH = (h % 2 == 0) ? h : h - 1;

        // Target standard 720p dimensions if larger, keeping aspect ratio
        if (targetW > 1280) {
            double scale = 1280.0 / targetW;
            targetW = 1280;
            targetH = (int) (targetH * scale);
            if (targetH % 2 != 0) targetH--;
        }

        BufferedImage rgbImage = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgbImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.drawImage(src, 0, 0, targetW, targetH, null);
        g.dispose();

        return rgbImage;
    }

    /**
     * Stops screen recording and encodes captured frames from disk to an MP4 video file.
     *
     * @return the generated MP4 File, or null if recording failed or no frames were captured
     */
    public static synchronized File stopRecording() {
        if (!isRecording.compareAndSet(true, false)) {
            return lastRecordedFile;
        }

        if (recordingTask != null) {
            recordingTask.cancel(false);
            recordingTask = null;
        }

        // Capture one final closing frame
        captureCurrentFrame();

        if (capturedFrameFiles.isEmpty()) {
            logger.warn("No frames captured during screen recording for [{}]", currentModule);
            cleanupTempFrames();
            currentDriver = null;
            return null;
        }

        File videoFile = null;
        try {
            Path recDir = Paths.get(AutomationConstants.RECORDINGS_DIR);
            Files.createDirectories(recDir);

            // Housekeeping: clean up old temporary recordings
            cleanupOldRecordings(recDir.toFile());

            String safeModule = ScreenshotManager.sanitize(currentModule);
            String safeTest = ScreenshotManager.sanitize(currentTestCase);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(recordingStartTime > 0 ? recordingStartTime : System.currentTimeMillis()));

            String fileName = safeModule + "_" + safeTest + "_" + timestamp + ".mp4";
            videoFile = recDir.resolve(fileName).toFile();

            logger.info("Encoding {} frames to MP4: {}", capturedFrameFiles.size(), videoFile.getAbsolutePath());

            // Encode frames to MP4 using JCodec by reading one temp frame at a time
            AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(videoFile, DEFAULT_FPS);
            List<File> framesCopy;
            synchronized (capturedFrameFiles) {
                framesCopy = new ArrayList<>(capturedFrameFiles);
            }

            for (File frameFile : framesCopy) {
                if (frameFile != null && frameFile.exists()) {
                    try {
                        BufferedImage frame = ImageIO.read(frameFile);
                        if (frame != null) {
                            encoder.encodeImage(frame);
                        }
                    } catch (Exception ex) {
                        logger.debug("Could not encode frame {}: {}", frameFile.getName(), ex.getMessage());
                    }
                }
            }
            encoder.finish();

            lastRecordedFile = videoFile;
            logger.info("🎬 Screen recording successfully saved: {} (Size: {} KB)", videoFile.getName(), (videoFile.length() / 1024));

        } catch (Exception e) {
            logger.error("Failed to encode screen recording video: {}", e.getMessage(), e);
        } finally {
            cleanupTempFrames();
            currentDriver = null;
        }

        return videoFile;
    }

    /**
     * Cleans up temporary frame files created during recording.
     */
    private static void cleanupTempFrames() {
        try {
            capturedFrameFiles.clear();
            if (tempFramesDir != null && tempFramesDir.exists()) {
                File[] files = tempFramesDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        try {
                            f.delete();
                        } catch (Exception ignored) {}
                    }
                }
                tempFramesDir.delete();
            }
        } catch (Exception e) {
            logger.debug("Temp frames cleanup: {}", e.getMessage());
        } finally {
            tempFramesDir = null;
        }
    }

    /**
     * Cleans up older recordings to ensure temporary storage does not grow unbounded.
     */
    private static void cleanupOldRecordings(File dir) {
        try {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".mp4"));
            if (files == null || files.length <= MAX_RECORDINGS_KEPT) return;

            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            int toDelete = files.length - MAX_RECORDINGS_KEPT;
            for (int i = 0; i < toDelete; i++) {
                try {
                    Files.deleteIfExists(files[i].toPath());
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            logger.debug("Recordings cleanup note: {}", e.getMessage());
        }
    }

    /**
     * Retrieves the latest video recording file for a given module, or the latest overall recording.
     *
     * @param moduleName target module name or "ALL" / "LATEST"
     * @return latest recording File or null
     */
    public static File getLatestRecording(String moduleName) {
        File dir = new File(AutomationConstants.RECORDINGS_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".mp4"));
        if (files == null || files.length == 0) {
            return null;
        }

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        if (moduleName == null || moduleName.isBlank() || "ALL".equalsIgnoreCase(moduleName) || "LATEST".equalsIgnoreCase(moduleName)) {
            return files[0];
        }

        String safePrefix = ScreenshotManager.sanitize(moduleName).toLowerCase();
        for (File file : files) {
            if (file.getName().toLowerCase().startsWith(safePrefix)) {
                return file;
            }
        }

        // Fallback to most recent
        return files[0];
    }

    /**
     * Retrieves all video files matching a given module name.
     */
    public static List<File> getRecordingsForModule(String moduleName) {
        File dir = new File(AutomationConstants.RECORDINGS_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".mp4"));
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        if (moduleName == null || moduleName.trim().isEmpty() || "ALL".equalsIgnoreCase(moduleName)) {
            return Arrays.asList(files);
        }

        String safePrefix = ScreenshotManager.sanitize(moduleName).toLowerCase();
        String shortPrefix = safePrefix.contains("_") ? safePrefix.substring(0, safePrefix.indexOf('_')) : safePrefix;

        List<File> matched = new ArrayList<>();
        for (File file : files) {
            String lowerName = file.getName().toLowerCase();
            if (lowerName.startsWith(safePrefix) || lowerName.contains(safePrefix) || lowerName.startsWith(shortPrefix)) {
                matched.add(file);
            }
        }

        return matched.isEmpty() ? Arrays.asList(files) : matched;
    }

    /**
     * Retrieves a specific video file by its exact name or base name.
     */
    public static File getRecordingFile(String fileName) {
        if (fileName == null || fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return null;
        }
        File dir = new File(AutomationConstants.RECORDINGS_DIR);
        File file = new File(dir, fileName);
        if (file.exists() && file.isFile()) {
            return file;
        }
        if (!fileName.toLowerCase().endsWith(".mp4")) {
            File withExt = new File(dir, fileName + ".mp4");
            if (withExt.exists() && withExt.isFile()) {
                return withExt;
            }
        }
        return null;
    }

    /**
     * Lists all available video recordings.
     */
    public static List<Map<String, Object>> listRecordings() {
        return listRecordings("ALL");
    }

    /**
     * Lists all available video recordings matching module or all.
     */
    public static List<Map<String, Object>> listRecordings(String moduleName) {
        List<File> files = getRecordingsForModule(moduleName);
        if (files.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (File file : files) {
            String name = file.getName();
            String base = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
            String[] parts = base.split("_");

            String module = parts.length > 0 ? parts[0] : "MODULE";
            String testCase = "TEST";
            for (String part : parts) {
                if (part.toUpperCase().startsWith("TC") || part.toUpperCase().contains("TC")) {
                    testCase = part;
                    break;
                }
            }
            if ("TEST".equals(testCase) && parts.length > 1) {
                testCase = parts[1];
            }

            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("fileName", name);
            map.put("displayName", base.replace("_", " "));
            map.put("module", module);
            map.put("testCase", testCase);
            map.put("size", file.length());
            map.put("fileSizeKb", file.length() / 1024);
            map.put("timestamp", sdf.format(new Date(file.lastModified())));
            map.put("lastModified", sdf.format(new Date(file.lastModified())));
            map.put("viewUrl", "/api/report/video/view/" + name);
            map.put("streamUrl", "/api/report/video/view/" + name);
            map.put("downloadUrl", "/api/report/video/download/" + name);
            list.add(map);
        }

        return list;
    }

    /**
     * Bundles multiple recording files into a single ZIP output stream.
     */
    public static void bundleRecordingsZip(List<File> files, OutputStream out) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            byte[] buffer = new byte[8192];
            for (File file : files) {
                if (!file.exists() || !file.isFile()) continue;
                ZipEntry entry = new ZipEntry(file.getName());
                zos.putNextEntry(entry);
                try (FileInputStream fis = new FileInputStream(file)) {
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                }
                zos.closeEntry();
            }
            zos.finish();
        }
    }

    /**
     * Checks if a recording is currently active.
     */
    public static boolean isRecording() {
        return isRecording.get();
    }
}
