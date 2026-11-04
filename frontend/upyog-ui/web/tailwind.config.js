const semanticPlugin = require("./tailwind.semantic");

module.exports = {
    content: [
        "./src/**/*.{js,jsx,ts,tsx}",

        "./micro-ui-internals/packages/css/src/**/*.{scss,css}",

        "./micro-ui-internals/packages/react-components/src/**/*.{js,jsx,ts,tsx}",

        "./micro-ui-internals/packages/modules/*/src/**/*.{js,jsx,ts,tsx}",
    ],

    theme: {
        screens: {
            dt: "780px",
        },

        extend: {
            spacing: {
                xs: "4px",
                sm: "8px",
                md: "16px",
                lg: "24px",
                xl: "32px",
                "2xl": "40px",

                "1/2": "50%",
                "1/3": "33.333333%",
                "1/4": "25%",
                "3/4": "75%",
            },

      colors: {
                primary: {
                    light: "#F18F5E",
                    main: "#a82227",
                    dark: "#C8602B",
                },

                secondary: "#22394D",

                text: {
                    primary: "#0B0C0C",
                    secondary: "#505A5F",
                },

                link: {
                    normal: "#1D70B8",
                    hover: "#003078",
                },

                border: "#D6D5D4",
                "input-border": "#464646",
                focus: "#a82227",
                error: "#D4351C",
                success: "#00703C",
                black: "#000000",

                grey: {
                    dark: "#9E9E9E",
                    mid: "#EEEEEE",
                    light: "#FAFAFA",
                    bg: "#E3E3E3",
                },

                white: "#FFFFFF",
            },

            fontFamily: {
                sans: ["Roboto", "sans-serif"],
                rc: ['"Roboto Condensed"', "sans-serif"],
            },

            fontWeight: {
                regular: "400",
                medium: "500",
                bold: "700",
            },
        },
    },

    plugins: [semanticPlugin],
};