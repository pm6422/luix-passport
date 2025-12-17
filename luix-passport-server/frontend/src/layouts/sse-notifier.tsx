import { useEffect, useRef } from "react"
import { useLoginUserStore } from "@/stores/login-user-store"
import { toast } from "sonner"
import { IconBellBolt } from "@tabler/icons-react"

export function SseNotifier() {
  const { loginUser } = useLoginUserStore()
  const eventSourceRef = useRef<EventSource | null>(null)
  const retryTimeoutRef = useRef<NodeJS.Timeout | null>(null)

  useEffect(() => {
    if (!loginUser.isAuthenticated) {
      return
    }

    const connect = () => {
      // Close existing connection if any
      if (eventSourceRef.current) {
        eventSourceRef.current.close()
      }

      const eventSource = new EventSource("/api/sse-notifiers/connect")
      eventSourceRef.current = eventSource

      eventSource.onopen = () => {
        console.log("SSE connection established")
      }

      eventSource.onmessage = (event) => {
        try {
          if (event.data) {
            let message = event.data
            // Try to parse as JSON first
            try {
              const data = JSON.parse(event.data)
              // If it's an object with message/description, use them
              if (typeof data === "object" && data !== null) {
                message = data.message || data.title || JSON.stringify(data)
              } else {
                message = String(data)
              }
            } catch {
              // Not JSON, use original data
            }

            toast(
              <div className="flex items-start gap-2">
                <IconBellBolt className="size-8 shrink-0 self-center me-2 text-primary" />
                <div className="flex flex-col gap-1">
                  <span className="font-bold">{message}</span>
                  <span>
                    Please go to{" "}
                    <a
                      href="/inbox"
                      className="font-medium text-primary underline-offset-4 hover:underline"
                    >
                      Inbox
                    </a>{" "}
                    to check.
                  </span>
                </div>
              </div>,
              { duration: 5000 }
            )
          }
        } catch (error) {
          console.error("Error processing SSE message:", error)
        }
      }

      eventSource.onerror = (_error) => {
        console.error("SSE connection error. Attempting to reconnect...")
        eventSource.close()

        // Retry connection after 5 seconds
        if (retryTimeoutRef.current) {
          clearTimeout(retryTimeoutRef.current)
        }
        retryTimeoutRef.current = setTimeout(connect, 5000)
      }
    }

    connect()

    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close()
        eventSourceRef.current = null
      }
      if (retryTimeoutRef.current) {
        clearTimeout(retryTimeoutRef.current)
      }
    }
  }, [loginUser.isAuthenticated])

  return null
}
