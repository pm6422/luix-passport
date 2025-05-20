import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { LayoutBody } from "@/layouts/layout-definitions"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from "@/components/ui/pagination"
import { Skeleton } from "@/components/ui/skeleton"
import { type UserNotification } from "@/domains/user-notification"
import { UserNotificationService } from "@/services/user-notification-service"

export default function Notifications() {
  const [selectedNotification, setSelectedNotification] = useState<UserNotification | null>(null)
  const [currentPage, setCurrentPage] = useState(0)
  const [notifications, setNotifications] = useState<UserNotification[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [totalPages, setTotalPages] = useState(1)

  useEffect(() => {
    fetchNotifications(currentPage)
  }, [currentPage])

  // 模拟数据获取
  const fetchNotifications = async (page: number) => {
    setIsLoading(true)
    UserNotificationService.find({
      page: page,
      size: 10,
    }).then(r => {
      setNotifications(r.data)
      const total = parseInt(r.headers["x-total-count"])
      setTotalPages(Math.ceil(total / 10))
      if (r.data.length > 0 && !selectedNotification) {
        setSelectedNotification(r.data[0])
      }
      setIsLoading(false)
    })
  }

  const markAsRead = async (id: string) => {
    try {
      await fetch(`/api/user-notifications/${id}/read`, { method: "POST" })
      setNotifications(notifications.map(n =>
        n.id === id ? { ...n, isRead: true } : n
      ))
      if (selectedNotification?.id === id) {
        setSelectedNotification({ ...selectedNotification, status: "READ" })
      }
    } catch (error) {
      console.error("Failed to mark as read", error)
    }
  }

  return (
    <LayoutBody className="space-y-4">
      <div className="flex h-full gap-4">
        {/* 左侧通知列表 */}
        <Card className="w-1/3">
          <CardHeader>
            <CardTitle>我的通知</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {isLoading ? (
              <div className="space-y-2 p-4">
                {Array(5).fill(0).map((_, i) => (
                  <Skeleton key={i} className="h-16 w-full" />
                ))}
              </div>
            ) : (
              <div className="divide-y">
                {notifications.map(notification => (
                  <div
                    key={notification.id}
                    className={`p-4 cursor-pointer hover:bg-gray-50 transition-colors ${
                      notification.id === selectedNotification?.id ? "bg-blue-50" : ""
                    } ${
                      notification.status === "UNREAD" ? "font-semibold" : ""
                    }`}
                    onClick={() => {
                      setSelectedNotification(notification)
                      if (notification.status === "UNREAD") {
                        markAsRead(notification.id)
                      }
                    }}
                  >
                    <h3 className="text-sm line-clamp-1">{notification.title}</h3>
                    <p className="text-xs text-gray-500 mt-1">
                      {new Date(notification.createdAt).toLocaleString()}
                    </p>
                    {notification.status === "UNREAD" && (
                      <span className="inline-block w-2 h-2 bg-blue-500 rounded-full ml-2"></span>
                    )}
                  </div>
                ))}
              </div>
            )}
            <div className="p-4 border-t">
              <Pagination>
                <PaginationContent>
                  <PaginationItem>
                    <PaginationPrevious
                      href="#"
                      onClick={(e) => {
                        e.preventDefault()
                        if (currentPage > 1) {
                          setCurrentPage(currentPage - 1)
                        }
                      }}
                      className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
                    />
                  </PaginationItem>

                  {Array.from({ length: Math.min(totalPages, 5) }, (_, i) => {
                    const page = i + 1
                    return (
                      <PaginationItem key={page}>
                        <PaginationLink
                          href="#"
                          onClick={(e) => {
                            e.preventDefault()
                            setCurrentPage(page)
                          }}
                          isActive={page === currentPage}
                        >
                          {page}
                        </PaginationLink>
                      </PaginationItem>
                    )
                  })}

                  <PaginationItem>
                    <PaginationNext
                      href="#"
                      onClick={(e) => {
                        e.preventDefault()
                        if (currentPage < totalPages) {
                          setCurrentPage(currentPage + 1)
                        }
                      }}
                      className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
                    />
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          </CardContent>
        </Card>

        {/* 右侧通知详情 */}
        <Card className="flex-1">
          <CardHeader>
            <CardTitle>通知详情</CardTitle>
          </CardHeader>
          <CardContent>
            {selectedNotification ? (
              <div>
                <div className="flex justify-between items-center mb-4">
                  <h2 className="text-xl font-bold">{selectedNotification.title}</h2>
                  <span className="text-sm text-gray-500">
                    {new Date(selectedNotification.createdAt).toLocaleString()}
                  </span>
                </div>
                <div className="prose max-w-none">
                  <p>{selectedNotification.content}</p>
                </div>
                <div className="mt-6">
                  <Button
                    variant="outline"
                    onClick={() => markAsRead(selectedNotification.id)}
                    disabled={selectedNotification.status === "READ"}
                  >
                    {selectedNotification.status === "READ" ? "已读" : "标记为已读"}
                  </Button>
                </div>
              </div>
            ) : (
              <div className="text-center py-8 text-gray-500">
                请从左侧选择一条通知查看详情
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </LayoutBody>
  )
}
