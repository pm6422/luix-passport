import { z } from "zod"

export const userNotificationSchema = z.object({
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
  modifiedAt: z.string()
})

export type UserNotification = z.infer<typeof userNotificationSchema>

export const initialUserNotificationState: UserNotification = {
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
  modifiedAt: ""
}