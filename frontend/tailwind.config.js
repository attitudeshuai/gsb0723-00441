/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}'
  ],
  theme: {
    extend: {
      colors: {
        accent: {
          DEFAULT: '#0052FF', // Electric Blue
          secondary: '#4D7CFF',
          foreground: '#FFFFFF',
        },
        primary: {
          DEFAULT: '#0F172A', // Slate 900
          light: '#334155',   // Slate 700
          dark: '#020617',    // Slate 950
        },
        success: '#10B981',
        warning: '#F59E0B',
        danger: '#EF4444',
        info: '#64748B',
        background: '#FAFAFA', // Warmer background
        foreground: '#0F172A',
        surface: '#FFFFFF',
        muted: {
          DEFAULT: '#F1F5F9',
          foreground: '#64748B',
        }
      },
      fontFamily: {
        display: ['Calistoga', 'serif'],
        sans: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
      boxShadow: {
        'soft': '0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03)',
        'soft-lg': '0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -2px rgba(0, 0, 0, 0.025)',
        'accent': '0 4px 14px rgba(0, 82, 255, 0.25)',
        'accent-lg': '0 8px 24px rgba(0, 82, 255, 0.35)',
      }
    }
  },
  plugins: [],
  corePlugins: {
    preflight: false
  }
}
