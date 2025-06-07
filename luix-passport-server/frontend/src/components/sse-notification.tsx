import { useEffect } from "react";
import { useStore } from "exome/react";
import { IconBellBolt } from "@tabler/icons-react";
import { toast } from "sonner";
import { appInfoStore } from "@/stores/app-info-store";
import { loginUserStore } from "@/stores/login-user-store";

let reconnectAttempts = 0;
const maxReconnectAttempts = 5;
const initialReconnectDelay = 1000;

export function SseNotification() {
  const { appInfo } = useStore(appInfoStore);
  const { loginUser } = useStore(loginUserStore);

  useEffect(() => {
    if (!loginUser.isAuthenticated) {
      return;
    }
    if (appInfo.ribbonProfile === "dev") {
      return;
    }

    const eventSource = setupSse();

    return () => {
      if (eventSource) {
        eventSource.close();
      }
    };
  }, [loginUser]);

  function setupSse(): EventSource | undefined {
    if (!window.EventSource) {
      toast.error("Your browser does NOT support Server-Sent Event!");
      return;
    }

    const eventSource = new EventSource("/api/sse/connect");

    eventSource.onopen = () => {
      reconnectAttempts = 0;
    };

    eventSource.onmessage = (event) => {
      setTimeout(() => {
        toast(
          <div className="flex items-start gap-2">
            <IconBellBolt className="size-8 shrink-0 self-center mr-2 text-primary" />
            <div className="flex flex-col gap-1">
              <span className="font-bold">{event.data}</span>
              <span>
                Please go to{" "}
                <a
                  href="/notifications"
                  className="font-medium text-primary underline-offset-4 hover:underline"
                >
                  notification center
                </a>{" "}
                to check.
              </span>
            </div>
          </div>,
          { duration: 5000 }
        );
      }, 2000);
    };

    eventSource.onerror = () => {
      eventSource.close();

      if (reconnectAttempts < maxReconnectAttempts) {
        const delay = initialReconnectDelay * Math.pow(2, reconnectAttempts);
        reconnectAttempts++;
        setTimeout(setupSse, delay);
      }
    };

    return eventSource;
  }

  return null;
}
