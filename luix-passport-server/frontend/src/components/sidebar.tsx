import { useEffect, useState, HTMLAttributes, Dispatch, SetStateAction } from "react"
import { useNavigate } from "react-router-dom"
import { IconChevronsLeft, IconMenu2, IconX } from "@tabler/icons-react"
import { Layout, LayoutHeader } from "../layouts/layout-definitions"
import { Button } from "./custom/button"
import { sidelinks } from "@/data/sidelinks"
import SidebarMenu from "./sidebar-menu"
import { cn } from "@/lib/utils"
import { useStore } from "exome/react"
import { appInfoStore } from "@/stores/app-info-store"
import { IntroY, MinusIntroY } from "@/components/custom/intro-motion"

interface SidebarProps extends HTMLAttributes<HTMLElement> {
  isCollapsed: boolean
  setIsCollapsed: Dispatch<SetStateAction<boolean>>
}

export default function Sidebar2({
  className,
  isCollapsed,
  setIsCollapsed,
}: SidebarProps) {
  const navigate = useNavigate();
  const [navOpened, setNavOpened] = useState(false)
  const { appInfo } = useStore(appInfoStore)

  /* Make body not scrollable when navBar is opened */
  useEffect(() => {
    if (navOpened) {
      document.body.classList.add("overflow-hidden")
    } else {
      document.body.classList.remove("overflow-hidden")
    }
  }, [navOpened])

  return (
    <aside
      className={cn(
        `fixed left-0 right-0 top-0 z-50 w-full border-r border-r-muted transition-[width] md:bottom-0 md:right-auto md:h-svh ${isCollapsed ? "md:w-14" : "md:w-64"}`,
        className
      )}
    >
      {/* Overlay in mobile */}
      <div
        onClick={() => setNavOpened(false)}
        className={`absolute inset-0 transition-[opacity] delay-100 duration-700 ${navOpened ? "h-svh opacity-50" : "h-0 opacity-0"} w-full bg-black md:hidden`}
      />

      <Layout>
        {/* Header */}
        <LayoutHeader className="sticky top-0 justify-between px-4 py-3 shadow md:px-4">
          <div className={`flex items-center ${!isCollapsed ? "gap-2" : ""}`}>
            <MinusIntroY>
              <img
                src="/assets/images/logos/site.svg"
                alt="favicon"
                className="relative m-auto"
                width={45}
                height={45}
                onClick={() => navigate("/console")}
              />
            </MinusIntroY>
            <div
                className={`flex flex-col ms-1 justify-end truncate ${isCollapsed ? "invisible w-0" : "visible w-auto"}`}
            >
              { appInfo.ribbonProfile && <span className="flex justify-end text-[0.6rem]"> {appInfo.ribbonProfile} env</span> }
              <IntroY>
                <img
                  src="/assets/images/logos/logo-text.svg"
                  alt="logo"
                  width={150}
                  height={50}
                  onClick={() => navigate("/console")}
                />
              </IntroY>
            </div>
          </div>

          {/* Toggle Button in mobile */}
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden"
            aria-label="Toggle Navigation"
            aria-controls="sidebar-menu"
            aria-expanded={navOpened}
            onClick={() => setNavOpened((prev) => !prev)}
          >
            {navOpened ? <IconX /> : <IconMenu2 />}
          </Button>
        </LayoutHeader>

        {/* Scrollbar width toggle button */}
        <Button
          onClick={() => setIsCollapsed((prev) => !prev)}
          size="icon"
          variant="outline"
          className="absolute -right-3 hidden mt-5 w-6 h-6 md:inline-flex"
        >
          <IconChevronsLeft
            stroke={1.5}
            className={`h-5 w-5 ${isCollapsed ? "rotate-180" : ""}`}
          />
        </Button>

        {/* Navigation links */}
        <SidebarMenu
          id="sidebar-menu"
          className={`h-full flex-1 overflow-auto ${navOpened ? "max-h-screen" : "max-h-0 py-0 md:max-h-screen md:py-2"}`}
          closeNav={() => setNavOpened(false)}
          isCollapsed={isCollapsed}
          links={sidelinks}
        />
      </Layout>
    </aside>
  )
}
