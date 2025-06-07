import { useSidebar } from "@/components/ui/sidebar"
import { IntroY, MinusIntroY } from "@/components/custom/intro-animation"
import { useNavigate } from "react-router-dom"
import { appInfoStore } from "@/stores/app-info-store"
import { useStore } from "exome/react"

export function AppSidebarHeader() {
  const navigate = useNavigate();
  const { open } = useSidebar()
  const { appInfo } = useStore(appInfoStore)

  return (
    <div className={`flex items-center ${open ? "gap-2" : ""}`}>
      <MinusIntroY>
        <img
          src="/assets/images/logos/site.svg"
          alt="favicon"
          className="relative m-auto cursor-pointer"
          width={45}
          height={45}
          onClick={() => navigate("/console")}
        />
      </MinusIntroY>
      <div
        className={`flex flex-col ms-1 justify-end truncate ${!open ? "invisible w-0" : "visible w-auto"}`}
      >
        { appInfo.ribbonProfile && <span className="flex justify-end text-[0.6rem]"> {appInfo.ribbonProfile} env</span> }
        <IntroY>
          <img
            src="/assets/images/logos/logo-text.svg"
            alt="logo"
            className="cursor-pointer"
            width={150}
            height={50}
            onClick={() => navigate("/console")}
          />
        </IntroY>
      </div>
    </div>
  )
}
