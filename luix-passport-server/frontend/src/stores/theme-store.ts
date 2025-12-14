import { create } from "zustand"
import { persist, createJSONStorage } from "zustand/middleware"

export type Theme = "dark" | "light" | "system"

interface ThemeState {
  theme: Theme
  setTheme: (theme: Theme) => void
}

export const useThemeStore = create<ThemeState>()(
  persist(
    (set) => ({
      theme: "light",
      setTheme: (theme) => {
        set({ theme })
        updateThemeDom(theme)
      },
    }),
    {
      name: "ui-theme",
      storage: createJSONStorage(() => localStorage),
      onRehydrateStorage: () => (state) => {
        if (state) {
          updateThemeDom(state.theme)
        }
      },
    }
  )
)

function updateThemeDom(theme: Theme) {
  if (typeof window === "undefined") return

  const root = window.document.documentElement
  root.classList.remove("light", "dark")

  if (theme === "system") {
    const systemTheme = window.matchMedia("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light"
    root.classList.add(systemTheme)
    return
  }

  root.classList.add(theme)
}
