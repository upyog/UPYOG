package middleware

import (
	"compress/gzip"
	"io"
	"net/http"
	"strings"
	"sync"

	"github.com/gin-gonic/gin"
)

// gzipPool reuses gzip.Writer instances to reduce allocations under high
// throughput. Writers are reset to the target io.Writer before use.
var gzipPool = sync.Pool{
	New: func() interface{} {
		w, _ := gzip.NewWriterLevel(io.Discard, gzip.DefaultCompression)
		return w
	},
}

// gzipResponseWriter wraps gin.ResponseWriter to transparently compress the
// response body using gzip.
type gzipResponseWriter struct {
	gin.ResponseWriter
	writer *gzip.Writer
}

// Write compresses and writes data to the underlying gzip writer.
func (g *gzipResponseWriter) Write(data []byte) (int, error) {
	return g.writer.Write(data)
}

// WriteString compresses and writes a string to the underlying gzip writer.
func (g *gzipResponseWriter) WriteString(s string) (int, error) {
	return g.writer.Write([]byte(s))
}

// Compression returns a middleware that gzip-compresses response bodies for
// clients that accept gzip encoding (via the Accept-Encoding header).
// It wraps the response writer with a gzip writer and sets the
// Content-Encoding: gzip header automatically.
func Compression() gin.HandlerFunc {
	return func(c *gin.Context) {
		if !strings.Contains(c.GetHeader("Accept-Encoding"), "gzip") {
			c.Next()
			return
		}

		gz := gzipPool.Get().(*gzip.Writer)
		defer gzipPool.Put(gz)

		gz.Reset(c.Writer)
		defer func() {
			// Flush and close the gzip stream so trailers are written.
			_ = gz.Close()
		}()

		c.Header("Content-Encoding", "gzip")
		c.Header("Vary", "Accept-Encoding")

		// Remove Content-Length since the compressed size is unknown upfront.
		c.Writer.Header().Del("Content-Length")

		c.Writer = &gzipResponseWriter{
			ResponseWriter: c.Writer,
			writer:         gz,
		}

		c.Next()

		// Ensure the status code is written before gzip close flushes.
		if !c.Writer.Written() {
			c.Writer.WriteHeaderNow()
		}
	}
}

// Ensure gzipResponseWriter satisfies the http.Flusher interface so that
// streaming responses work correctly.
var _ http.Flusher = (*gzipResponseWriter)(nil)

// Flush implements http.Flusher for streaming compatibility.
func (g *gzipResponseWriter) Flush() {
	_ = g.writer.Flush()
	if flusher, ok := g.ResponseWriter.(http.Flusher); ok {
		flusher.Flush()
	}
}
