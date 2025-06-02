import http from "@/axios"
import { ContactFormSchema } from "@/views/site/contact-us"

export class ContactService {
  constructor() {
  }
  

  public static create(model: ContactFormSchema): Promise<void> {
    return http.post("/open-api/notifications/contact", model)
  }
}