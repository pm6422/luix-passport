import { z } from "zod"

export const inboxSchema = z.object({
  id: z.string(),
  notificationId: z.string(), 
  title: z.string(),
  content: z.string(),
  type: z.string(),
  sender: z.string(),
  senderEmail: z.string(),
  receiverId: z.string(),
  status: z.string(),
  active: z.boolean(),
  createdAt: z.string(),
  updatedAt: z.string()
})

export type Inbox = z.infer<typeof inboxSchema>

export const initialInboxState: Inbox = {
  id: "",
  notificationId: "",
  title: "",
  content: "",
  type: "",
  sender: "",
  senderEmail: "",
  receiverId: "",
  status: "",
  active: true,
  createdAt: "",
  updatedAt: ""
}