/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,js}", "./src/*.{html,js}", "/client/src/*.{html,js}", "/client/src/**/*.{html,js}"],
  corePlugins: {preflight: false},
  theme: {
    extend: {},
  },
  plugins: [],
}