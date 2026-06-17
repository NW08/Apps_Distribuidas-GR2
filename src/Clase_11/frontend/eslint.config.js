import js from "@eslint/js";
import globals from "globals";
import eslintReact from "@eslint-react/eslint-plugin";
import {defineConfig} from "eslint/config";

export default defineConfig([
    js.configs.recommended,
    {
        files: ["**/*.{js,mjs,cjs,jsx}"],
        languageOptions: {
            globals: globals.browser
        }
    },

    {
        files: ["**/*.{js,jsx,mjs,cjs}"],
        plugins: {
            "@eslint-react": eslintReact
        },
        rules: {
            ...eslintReact.configs.recommended.rules,
            // Ejemplo: "@eslint-react/dom/no-unknown-property": "off"
        }
    }
]);