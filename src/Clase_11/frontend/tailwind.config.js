/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#2E86C1',
        'primary-dark': '#2471A3',
        success: '#27AE60',
        'success-dark': '#1E8449',
        warning: '#E67E22',
        muted: '#95A5A6',
        'bg-app': '#EBF5FB',
      },
      fontFamily: {
        sans: ['Inter', 'Poppins', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
