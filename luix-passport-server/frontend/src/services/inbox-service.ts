import http from "@/axios"
import { type Inbox } from "@/domains/inbox"
import {AxiosResponse} from "axios"
import { Page } from "@/domains/page"

export class InboxService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Page<Inbox>>> {
    return http.get("/api/inbox", { params: reqParams })
  }

  public static markAsRead(id: string): Promise<void> {
    return http.put("/api/inbox/mark-as-read/" + id)
  }

  public static countUnread(): Promise<AxiosResponse<number>> {
    return http.get("/api/inbox/unread-count")
  }
}