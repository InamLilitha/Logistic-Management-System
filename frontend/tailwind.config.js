/* 24/08/2026
 *tailwind.config.js
 *tailwind configuration in frontend folder
 *Maghdie Petersen 230600204
 *  */

/** @type {import('tailwindcss').Config}*/
export default {
    content: [
      "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                lms: {
                    navy: '#1E3A8A', //Headers & sidebar
                    action: '#2563EB', //Primary buttons
                    ice: '#DBEAFE', //Row hover state
                    success: '#16A34A', //Badges & online states
                    danger: '#DC2626', //Delete & error actions
                    bg: '#F1F5F9', //Main background slate
                    textMain: '#0F172A', //Primary text
                    textMuted: '#64748B' //Subtext
                }
            }
        },
    },
    plugins: [],
}