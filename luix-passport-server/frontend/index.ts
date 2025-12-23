import { createRoot } from "react-dom/client"
import { createElement } from "react"
import { ManagementService } from "./src/services/management-service"
import Main from "./src/main"
import { useAppInfoStore } from "./src/stores/app-info-store"
import dayjs from "dayjs"
import utc from "dayjs/plugin/utc"
import timezone from "dayjs/plugin/timezone"

dayjs.extend(utc)
dayjs.extend(timezone)

ManagementService.getAppInfo().then(appInfo => {
  useAppInfoStore.getState().setAppInfo(appInfo)

  createRoot(document.getElementById("root")!)
    .render(createElement(Main))
})
