import React, { useState, useEffect } from "react"
import { cn } from "@/lib/utils"
import { LayoutBody } from "@/layouts/layout-definitions"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from "@/components/ui/pagination"
import { Skeleton } from "@/components/ui/skeleton"
import { type UserNotification } from "@/domains/user-notification"
import { UserNotificationService } from "@/services/user-notification-service"
import { DateTime } from "@/components/custom/date-time"
import { Badge } from "@/components/ui/badge"
import { Separator } from "@/components/ui/separator"
import { IconBellRinging, IconSearch } from "@tabler/icons-react"
import { Input } from "@/components/ui/input"

export default function Notifications() {
  const [selectedNotification, setSelectedNotification] = useState<UserNotification | null>(null)
  const [currentPage, setCurrentPage] = useState(0)
  const [notifications, setNotifications] = useState<UserNotification[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [totalPages, setTotalPages] = useState(1)
  const [totalCount, setTotalCount] = useState(0)
  const [keyword, setKeyword] = useState("")

  useEffect(() => {
    loadNotifications(currentPage)
  }, [currentPage])

  function loadNotifications(pageNo: number = 0, sorts: Array<string> = ["createdAt,desc"]): void {
    setIsLoading(true)
    UserNotificationService.find({
      page: pageNo - 1,
      size: 10,
      sort: sorts,
      keyword: keyword
    }).then(r => {
      setNotifications(r.data)
      const total = parseInt(r.headers["x-total-count"])
      setTotalCount(total)
      setTotalPages(Math.ceil(total / 10))
      if (r.data.length > 0 && !selectedNotification) {
        setSelectedNotification(r.data[0])
      }
      setIsLoading(false)
    })
  }

  function markAsRead(id: string): void {
    UserNotificationService.markAsRead(id).then(() => {
      setNotifications(notifications.map(n =>
        n.id === id ? { ...n, status: "READ" } : n
      ))
      if (selectedNotification?.id === id) {
        setSelectedNotification({ ...selectedNotification, status: "READ" })
      }
    })
  }

  function search(e: React.KeyboardEvent<HTMLInputElement>) : void {
    if (e.key === 'Enter') {
      loadNotifications(currentPage);
    }
  }

  return (
    <LayoutBody className="space-y-4">
      <div className="flex h-full gap-4">
        {/* Notifications List */}
        <Card className="w-full lg:w-1/3">
          <CardHeader>
            <CardTitle>
                <div className="flex items-center justify-between">
                  <div>
                    <div className="flex">
                      <IconBellRinging className="size-4 mr-2"/>
                      Notifications
                    </div>
                  </div>
                  <Badge variant="outline" className="h-4">{totalCount}</Badge>
                </div>
            </CardTitle>
          </CardHeader>
          <Separator/>
          <CardContent className="p-0">
            <div className="p-4">
              <div className="relative">
                <IconSearch className="absolute left-2 top-2.5 size-4 text-muted-foreground"/>
                <Input
                  placeholder="Search" className="pl-8"
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                  onKeyDown={search}
                />
              </div>
            </div>

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
                    className={cn(
                      "p-4 cursor-pointer hover:bg-muted transition-colors",
                      notification.id === selectedNotification?.id && "bg-accent border-l-4 border-primary",
                      notification.status === "UNREAD" && "font-medium"
                    )}
                    onClick={() => {
                      setSelectedNotification(notification)
                      if (notification.status === "UNREAD") {
                        markAsRead(notification.id)
                      }
                    }}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                      <h3 className="text-sm line-clamp-1">{notification.title}</h3>
                      <p className="text-xs text-muted-foreground mt-1">
                        <DateTime value={notification.createdAt}/>
                      </p>
                      </div>
                      {notification.status === "UNREAD" && (
                        <span className="inline-block w-2 h-2 bg-blue-500 rounded-full ml-2"></span>
                      )}
                    </div>
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
                      className={currentPage <= 1 ? "pointer-events-none opacity-50" : ""}
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
                      className={currentPage >= totalPages || totalPages <= 1 ? "pointer-events-none opacity-50" : ""}
                      aria-disabled={currentPage >= totalPages || totalPages <= 1}
                    />
                  </PaginationItem>
                </PaginationContent>
              </Pagination>
            </div>
          </CardContent>
        </Card>

        {/* Notification Details */}
        <Card className="flex-1 hidden lg:block">
          <CardHeader>
            <CardTitle>Content</CardTitle>
          </CardHeader>
          <Separator/>
          <CardContent>
            {selectedNotification ? (
              <div>
                <div className="flex justify-between items-center mb-4 mt-5">
                  <h2 className="text-xl font-bold">{selectedNotification.title}</h2>
                  <DateTime value={selectedNotification.createdAt} className="text-sm text-muted-foreground"/>
                </div>

                {/* Add sender information section */}
                {(selectedNotification.sender || selectedNotification.senderEmail) && (
                  <div className="mb-6 p-4 bg-muted/50 rounded-lg">
                    <h3 className="text-sm font-medium mb-2">From</h3>
                    <div className="space-y-2">
                      {selectedNotification.sender && (
                        <p className="text-sm">
                          <span className="text-muted-foreground">Name: </span>
                          {selectedNotification.sender}
                        </p>
                      )}
                      {selectedNotification.senderEmail && (
                        <p className="text-sm">
                          <span className="text-muted-foreground">Email: </span>
                          <a
                            href={`mailto:${selectedNotification.senderEmail}`}
                            className="text-primary hover:underline"
                          >
                            {selectedNotification.senderEmail}
                          </a>
                        </p>
                      )}
                    </div>
                  </div>
                )}

                <div className="prose max-w-none">
                  <p>{selectedNotification.content}</p>
                </div>
              </div>
            ) : (
              <div className="text-center py-8 text-muted-foreground">
                No notification selected
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </LayoutBody>
  )
}
