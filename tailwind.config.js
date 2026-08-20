/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    './app/src/main/java/**/*.kt',
    './src/**/*.{js,ts,jsx,tsx,html}'
  ],
  theme: {
    extend: {
      colors: {
        civic: {
          navy: {
            dark: '#1A237E',      // Deep Indigo Navy
            primary: '#283593',   // Primary Indigo Accent
            light: '#3F51B5',     // Vibrant Indigo
            container: '#EEF2FF', // Indigo 50 Surface
            border: '#C7D2FE'     // Indigo 200 Border
          },
          green: {
            primary: '#2E7D32',  // Emerald Green (#2E7D32)
            light: '#4CAF50',    // Crisp Green
            container: '#E8F5E9',// Light Green 50
            dark: '#1B5E20'      // Active Deep Forest Green (#1B5E20)
          },
          amber: {
            DEFAULT: '#F59E0B',
            container: '#FEF3C7',
            text: '#B45309',
            dark: '#D97706'
          },
          red: {
            DEFAULT: '#DC2626',
            container: '#FEE2E2',
            text: '#991B1B',
            dark: '#B91C1C'
          },
          slate: {
            50: '#F8FAFC',
            100: '#F1F5F9',
            200: '#E2E8F0',
            300: '#CBD5E1',
            400: '#94A3B8',
            500: '#64748B',
            600: '#475569',
            700: '#334155',
            800: '#1E293B',
            900: '#0F172A'
          }
        }
      },
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'Inter', 'system-ui', 'sans-serif'],
        display: ['Outfit', 'Plus Jakarta Sans', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace']
      },
      fontSize: {
        '2xs': ['0.625rem', { lineHeight: '0.875rem' }],
        'xs': ['0.75rem', { lineHeight: '1rem' }],
        'sm': ['0.875rem', { lineHeight: '1.25rem' }],
        'base': ['1rem', { lineHeight: '1.5rem' }],
        'lg': ['1.125rem', { lineHeight: '1.75rem' }],
        'xl': ['1.25rem', { lineHeight: '1.75rem' }],
        '2xl': ['1.5rem', { lineHeight: '2rem' }],
        '3xl': ['1.875rem', { lineHeight: '2.25rem' }]
      },
      borderRadius: {
        'sm': '0.375rem',
        'md': '0.5rem',
        'lg': '0.75rem',
        'xl': '1rem',
        '2xl': '1.125rem',
        '3xl': '1.5rem',
        'pill': '9999px'
      },
      boxShadow: {
        'card': '0 1px 3px 0 rgba(15, 23, 42, 0.06), 0 1px 2px -1px rgba(15, 23, 42, 0.06)',
        'card-hover': '0 10px 15px -3px rgba(15, 23, 42, 0.08), 0 4px 6px -4px rgba(15, 23, 42, 0.04)',
        'button': '0 1px 2px 0 rgba(15, 23, 42, 0.05)',
        'floating': '0 20px 25px -5px rgba(15, 23, 42, 0.1), 0 8px 10px -6px rgba(15, 23, 42, 0.1)'
      }
    }
  },
  plugins: []
};
