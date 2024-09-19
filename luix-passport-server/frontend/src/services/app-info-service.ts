import axios from "axios"
import type { AppInfo } from "@/stores/app-info-provider"

export class AppInfoService {

  constructor() {
  }

  public static async load(): Promise<AppInfo | null> {
    try {
      const res = await axios.get<AppInfo>("management/info")
      return res.data
    } catch (error) {
      console.error(error)
      return null
    }
  }
}
