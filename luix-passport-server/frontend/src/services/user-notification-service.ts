import http from "@/axios"
import { type UserNotification } from "@/domains/user-notification"
import {AxiosResponse} from "axios"

export class UserNotificationService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Array<UserNotification>>> {
    return http.get("api/user-notifications", { params: reqParams })
  }

  public static markAsRead(id: string): Promise<void> {
    return http.put("api/user-notifications/mark-as-read/" + id)
  }

  public static countUnread(): Promise<AxiosResponse<number>> {
    return http.get("api/user-notifications/unread-count")
  }
}