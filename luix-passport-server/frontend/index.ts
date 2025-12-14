import { createRoot } from "react-dom/client"
import { createElement } from "react"
import { ManagementService } from "./src/services/management-service"
import { AccountService } from "./src/services/account-service"
import Main from "./src/main"
import { useAppInfoStore } from "./src/stores/app-info-store"
import { useLoginUserStore } from "./src/stores/login-user-store"
import dayjs from "dayjs"
import utc from "dayjs/plugin/utc"
import timezone from "dayjs/plugin/timezone"

dayjs.extend(utc)
dayjs.extend(timezone)

Promise.all([ManagementService.getAppInfo(), AccountService.getCurrentUser()]).then(results => {
  useAppInfoStore.getState().setAppInfo(results[0])
  useLoginUserStore.getState().setLoginUser(results[1])

  createRoot(document.getElementById("root")!)
    .render(createElement(Main))
})
