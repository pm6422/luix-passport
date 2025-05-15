import { createRoot } from "react-dom/client"
import { createElement } from "react"
import { AppInfoService } from "./src/services/app-info-service"
import Main from "./src/main"

Promise.all([AppInfoService.load()]).then(results => {
    createRoot(document.getElementById("root")!)
        .render(createElement(Main, { appInfo: results[0] }))
})
