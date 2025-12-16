import { useState, useCallback, useEffect, useMemo, useRef } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { type UserNotification } from "@/domains/user-notification"
import { UserNotificationService } from "@/services/user-notification-service"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import { IconBell, IconMail, IconCheck, IconClock, IconX } from "@tabler/icons-react"
import dayjs from "dayjs"
import relativeTime from "dayjs/plugin/relativeTime"
import isToday from "dayjs/plugin/isToday"
import isYesterday from "dayjs/plugin/isYesterday"

// Extend dayjs plugins
dayjs.extend(relativeTime)
dayjs.extend(isToday)
dayjs.extend(isYesterday)

export default function InboxPage() {
  const [data, setData] = useState([] as Array<UserNotification>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)
  const [loading, setLoading] = useState(false)
  const [searchKeyword, setSearchKeyword] = useState("")
  const debounceRef = useRef<NodeJS.Timeout | null>(null)

  const loadPage = useCallback((
    pageNo: number = 0,
    pageSize: number = 20, // Increased page size for list view
    sorts: Array<string> = ["createdAt,desc"],
    criteria: any = {}
  ): void => {
    setLoading(true)
    setCurrentPageNo(pageNo)
    UserNotificationService.find({
      page: pageNo,
      size: pageSize,
      sort: sorts,
      keyword: criteria.keyword || null
    }).then(r => {
      setData(r.data)
      const total = parseInt(r.headers["x-total-count"])
      setTotalCount(total)
      setTotalPages(Math.ceil(total / pageSize))
    }).finally(() => {
      setLoading(false)
    })
  }, [])

  const triggerSearch = useCallback((keyword: string) => {
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }

    debounceRef.current = setTimeout(() => {
      loadPage(0, undefined, undefined, {
        keyword: keyword || undefined
      })
    }, 500)
  }, [loadPage])

  const handleSearchChange = (value: string) => {
    setSearchKeyword(value)
    triggerSearch(value)
  }

  const handleResetSearch = () => {
    setSearchKeyword("")
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }
    loadPage(0, undefined, undefined, {})
  }

  // Initial load
  useEffect(() => {
    loadPage()
  }, [loadPage])

  function markAsRead(row: UserNotification, e?: React.MouseEvent): Promise<void> {
    e?.stopPropagation()
    return UserNotificationService.markAsRead(row.id)
      .then(() => {
        // Optimistic update
        setData(prev => prev.map(item => 
          item.id === row.id ? { ...item, status: "READ" } : item
        ))
      })
      .catch((error) => {
        toast.error(getErrorMessage(error))
      })
  }

  // Group data by date
  const groupedData = useMemo(() => {
    const groups: { label: string; items: UserNotification[] }[] = []
    
    data.forEach(item => {
      const date = dayjs(item.createdAt)
      let label = "Earlier"
      
      if (date.isToday()) {
        label = "Today"
      } else if (date.isYesterday()) {
        label = "Yesterday"
      } else if (date.isAfter(dayjs().subtract(7, 'day'))) {
        label = "This Week"
      }
      
      const existingGroup = groups.find(g => g.label === label)
      if (existingGroup) {
        existingGroup.items.push(item)
      } else {
        groups.push({ label, items: [item] })
      }
    })
    
    return groups
  }, [data])

  const renderPagination = () => {
    if (totalPages <= 1) return null
    
    const pages = []
    const maxVisiblePages = 5
    let startPage = Math.max(0, currentPageNo - Math.floor(maxVisiblePages / 2))
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1)
    
    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(0, endPage - maxVisiblePages + 1)
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i)
    }

    return (
      <Pagination className="mt-8 mb-4">
        <PaginationContent>
          <PaginationItem>
            <PaginationPrevious 
              onClick={() => currentPageNo > 0 && loadPage(currentPageNo - 1)}
              className={currentPageNo === 0 ? "pointer-events-none opacity-50" : "cursor-pointer"}
            />
          </PaginationItem>
          
          {startPage > 0 && (
            <>
              <PaginationItem>
                <PaginationLink onClick={() => loadPage(0)}>1</PaginationLink>
              </PaginationItem>
              {startPage > 1 && (
                <PaginationItem>
                  <PaginationEllipsis />
                </PaginationItem>
              )}
            </>
          )}

          {pages.map(page => (
            <PaginationItem key={page}>
              <PaginationLink 
                isActive={page === currentPageNo}
                onClick={() => loadPage(page)}
                className="cursor-pointer"
              >
                {page + 1}
              </PaginationLink>
            </PaginationItem>
          ))}

          {endPage < totalPages - 1 && (
            <>
              {endPage < totalPages - 2 && (
                <PaginationItem>
                  <PaginationEllipsis />
                </PaginationItem>
              )}
              <PaginationItem>
                <PaginationLink onClick={() => loadPage(totalPages - 1)}>{totalPages}</PaginationLink>
              </PaginationItem>
            </>
          )}

          <PaginationItem>
            <PaginationNext 
              onClick={() => currentPageNo < totalPages - 1 && loadPage(currentPageNo + 1)}
              className={currentPageNo >= totalPages - 1 ? "pointer-events-none opacity-50" : "cursor-pointer"}
            />
          </PaginationItem>
        </PaginationContent>
      </Pagination>
    )
  }

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="flex flex-col h-full max-w-4xl mx-auto w-full">
        {/* Header Section */}
        <div className="flex items-center justify-between p-6 pb-2 shrink-0">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">Inbox</h1>
            <p className="text-muted-foreground mt-1">
              You have {totalCount} notifications
            </p>
          </div>
          <div className="w-[300px] flex items-center justify-end gap-2">
            <Input
              placeholder="Filter notifications..."
              value={searchKeyword}
              onChange={(e) => handleSearchChange(e.target.value)}
              className="h-8 w-full"
            />
            {searchKeyword && (
              <Button
                variant="ghost"
                size="sm"
                onClick={handleResetSearch}
                className="px-2"
              >
                <IconX className="h-4 w-4" />
              </Button>
            )}
          </div>
        </div>

        {/* Content Section */}
        <div className="flex-1 overflow-auto min-h-0 p-6 pt-2">
          {loading && data.length === 0 ? (
             <div className="flex flex-col items-center justify-center h-64 text-muted-foreground">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mb-4"></div>
                Loading notifications...
             </div>
          ) : data.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-64 text-muted-foreground border-2 border-dashed rounded-xl mt-8">
              <IconBell className="h-12 w-12 mb-4 opacity-20" />
              <p>No notifications found</p>
            </div>
          ) : (
            <div className="space-y-8">
              {groupedData.map((group) => (
                <div key={group.label} className="animate-in fade-in slide-in-from-bottom-4 duration-500">
                  <div className="flex items-center gap-2 mb-4">
                    <h2 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider bg-background px-2">
                      {group.label}
                    </h2>
                    <div className="h-px bg-border flex-1" />
                  </div>
                  
                  <div className="space-y-1">
                    {group.items.map((notification) => (
                      <div
                        key={notification.id}
                        onClick={(e) => notification.status === 'UNREAD' && markAsRead(notification, e)}
                        className={cn(
                          "group relative flex items-center gap-4 p-4 rounded-xl transition-all duration-200 border border-transparent",
                          "hover:bg-muted/50 hover:border-border",
                          notification.status === "UNREAD" ? "bg-primary/5 cursor-pointer shadow-sm border-primary/20" : "bg-background"
                        )}
                      >
                        {/* Unread Indicator */}
                        {notification.status === "UNREAD" && (
                          <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1.5 h-full bg-primary rounded-l-xl" />
                        )}

                        {/* Icon */}
                        <div className={cn(
                          "flex h-10 w-10 shrink-0 items-center justify-center rounded-full",
                          notification.type === "SYSTEM" 
                            ? "bg-blue-500/10 text-blue-500" 
                            : "bg-orange-500/10 text-orange-500"
                        )}>
                          {notification.type === "SYSTEM" ? <IconBell size={18} /> : <IconMail size={18} />}
                        </div>

                        {/* Content */}
                        <div className="flex-1 min-w-0 py-0.5">
                          <div className="flex items-center justify-between gap-4 mb-1">
                            <h3 className={cn(
                              "text-sm font-medium truncate",
                              notification.status === "UNREAD" ? "text-foreground font-semibold" : "text-muted-foreground"
                            )}>
                              {notification.title}
                            </h3>
                            <div className="flex items-center gap-2 text-xs text-muted-foreground shrink-0">
                              <IconClock size={12} />
                              <span>{dayjs(notification.createdAt).fromNow()}</span>
                            </div>
                          </div>
                          
                          <p className={cn(
                            "text-sm line-clamp-2",
                            notification.status === "UNREAD" ? "text-foreground/80" : "text-muted-foreground/70"
                          )}>
                            {notification.content}
                          </p>
                          
                          <div className="mt-2 flex items-center gap-2 text-xs text-muted-foreground/60">
                            {notification.senderEmail ? (
                              <span>From: {notification.sender} &lt;{notification.senderEmail}&gt;</span>
                            ) : (
                              <span>From: System</span>
                            )}
                          </div>
                        </div>

                        {/* Actions */}
                        <div className="flex items-center self-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                          {notification.status === "UNREAD" && (
                            <Button
                              variant="ghost"
                              size="icon"
                              className="h-8 w-8 text-muted-foreground hover:text-primary hover:bg-primary/10 rounded-full"
                              onClick={(e) => markAsRead(notification, e)}
                              title="Mark as read"
                            >
                              <IconCheck size={16} />
                            </Button>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
          
          {renderPagination()}
        </div>
      </div>
    </LayoutBody>
  )
}
