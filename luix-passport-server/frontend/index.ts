import { createRoot } from "react-dom/client"
import { createElement } from "react"
import { ManagementService } from "./src/services/management-service"
import { AccountService } from "./src/services/account-service"
import Main from "./src/main"
import { useAppInfoStore } from "./src/stores/app-info-store"
import { useLoginUserStore } from "./src/stores/login-user-store"

Promise.all([ManagementService.getAppInfo(), AccountService.getCurrentUser()]).then(results => {
  useAppInfoStore.getState().setAppInfo(results[0])
  useLoginUserStore.getState().setLoginUser(results[1])

  createRoot(document.getElementById("root")!)
    .render(createElement(Main))
})
