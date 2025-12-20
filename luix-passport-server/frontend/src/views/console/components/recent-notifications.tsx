import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Inbox } from "lucide-react"
import { InboxService } from "@/services/inbox-service"
import { type Inbox as InboxType } from "@/domains/inbox"
import { UserDateTime } from "@/components/custom/display/user-date-time"
import { useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"

export function RecentNotifications() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [messages, setMessages] = useState<InboxType[]>([])
  const [unreadCount, setUnreadCount] = useState(0)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [inboxRes, unreadRes] = await Promise.all([
          InboxService.find({ page: 0, size: 1, sort: ["status,desc", "createdAt,desc"] }),
          InboxService.countUnread()
        ])
        setMessages(inboxRes.data.content)
        setUnreadCount(unreadRes.data)
      } catch (error) {
        console.error("Failed to load notifications:", error)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])


  const handleMessageClick = async (msg: InboxType) => {
    if (msg.status === 'UNREAD') {
      try {
        await InboxService.markAsRead(msg.id)
        const updatedMessages = messages.map(m => m.id === msg.id ? { ...m, status: 'READ' } : m)
        setMessages(updatedMessages)
        setUnreadCount(prev => Math.max(0, prev - 1))
      } catch (e) {
        console.error(e)
      }
    }
    navigate("/inbox")
  }

  return (
    <Card className="border border-border/50 shadow-sm overflow-hidden text-clip bg-card">
      <CardHeader className="pb-3 flex flex-row items-center justify-between border-b border-border/50">
        <div>
          <CardTitle className="text-sm font-bold flex items-center">
            <Inbox className="mr-2 h-4 w-4 text-primary" />
            Recent Notifications
          </CardTitle>
        </div>
        {unreadCount > 0 && (
          <Badge variant="outline" className="text-[10px] bg-primary/5 text-primary border-primary/20">
            {unreadCount} New
          </Badge>
        )}
      </CardHeader>
      <CardContent className="p-0">
        <div className="divide-y divide-border/50">
          {loading ? (
            [1, 2, 3].map(i => <div key={i} className="p-4 space-y-2"><div className="h-4 w-3/4 bg-muted animate-pulse rounded" /><div className="h-3 w-1/2 bg-muted/60 animate-pulse rounded" /></div>)
          ) : messages.length > 0 ? (
            messages.map((msg: InboxType) => (
              <div
                key={msg.id}
                className={`relative p-4 hover:bg-muted/30 transition-colors cursor-pointer group ${msg.status === 'UNREAD' ? 'bg-primary/[0.02]' : ''}`}
                onClick={() => handleMessageClick(msg)}
              >
                <div className="flex justify-between items-start gap-2">
                  <h4 className={`text-xs font-semibold truncate flex items-center gap-2 ${msg.status === 'UNREAD' ? 'text-foreground' : 'text-muted-foreground'}`}>
                    {msg.status === "UNREAD" && (
                      <div className="w-2 h-2 rounded-full bg-primary flex-shrink-0" />
                    )}
                    {msg.title}
                  </h4>
                  <span className="text-[10px] text-muted-foreground whitespace-nowrap">
                    <UserDateTime value={msg.createdAt} type="datetime" />
                  </span>
                </div>
                <p className="text-[10px] text-muted-foreground line-clamp-1 mt-1 group-hover:text-foreground/70 transition-colors">
                  {msg.content}
                </p>
              </div>
            ))
          ) : (
            <div className="p-8 text-center text-clip">
              <p className="text-[10px] text-muted-foreground italic">No messages found.</p>
            </div>
          )}
        </div>
        <Button variant="ghost" className="w-full rounded-none h-10 text-xs border-t border-border/50 hover:bg-muted/50" onClick={() => navigate("/inbox")}>
          View All Messages
        </Button>
      </CardContent>
    </Card>
  )
}
