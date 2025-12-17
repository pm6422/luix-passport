import { useState, useCallback, useEffect, useMemo, useRef } from "react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { type Inbox } from "@/domains/inbox"
import { InboxService } from "@/services/inbox-service"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { UserDateTime } from "@/components/custom/display/user-date-time"
import { IconBell, IconMail, IconX } from "@tabler/icons-react"
import dayjs from "dayjs"
import relativeTime from "dayjs/plugin/relativeTime"
import isToday from "dayjs/plugin/isToday"
import isYesterday from "dayjs/plugin/isYesterday"

// Extend dayjs plugins
dayjs.extend(relativeTime)
dayjs.extend(isToday)
dayjs.extend(isYesterday)

export default function InboxPage() {
  const [data, setData] = useState([] as Array<Inbox>)
  const [totalCount, setTotalCount] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [currentPageNo, setCurrentPageNo] = useState(0)
  const [loading, setLoading] = useState(false)
  const [searchKeyword, setSearchKeyword] = useState("")
  const [selectedId, setSelectedId] = useState<string | null>(null)
  const debounceRef = useRef<NodeJS.Timeout | null>(null)

  const selectedItem = useMemo(() =>
    data.find(item => item.id === selectedId),
    [data, selectedId]
  )

  const loadPage = useCallback((
    pageNo: number = 0,
    pageSize: number = 20,
    sorts: Array<string> = ["createdAt,desc"],
    criteria: any = {}
  ): void => {
    setLoading(true)
    setCurrentPageNo(pageNo)
    InboxService.find({
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

  function markAsRead(id: string, e?: React.MouseEvent): Promise<void> {
    e?.stopPropagation()
    return InboxService.markAsRead(id)
      .then(() => {
        // Optimistic update
        setData(prev => prev.map(item =>
          item.id === id ? { ...item, status: "READ" } : item
        ))
      })
      .catch((error) => {
        toast.error(getErrorMessage(error))
      })
  }

  // Group data by date
  const groupedData = useMemo(() => {
    const groups: { label: string; items: Inbox[] }[] = []

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

    // Simplified pagination for sidebar
    return (
      <div className="flex items-center justify-between px-4 py-2 border-t text-sm">
        <Button
          variant="ghost"
          size="sm"
          disabled={currentPageNo === 0}
          onClick={() => loadPage(currentPageNo - 1)}
        >
          Previous
        </Button>
        <span className="text-muted-foreground">
          {currentPageNo + 1} / {totalPages}
        </span>
        <Button
          variant="ghost"
          size="sm"
          disabled={currentPageNo >= totalPages - 1}
          onClick={() => loadPage(currentPageNo + 1)}
        >
          Next
        </Button>
      </div>
    )
  }

  return (
    <LayoutBody className="flex flex-col h-[calc(100vh-64px)] overflow-hidden" fixedHeight>
      <div className="flex h-full w-full border rounded-xl overflow-hidden bg-background shadow-sm">
        {/* Left Sidebar - Message List */}
        <div className="w-[400px] flex flex-col border-r bg-muted/10 shrink-0">
          {/* Header */}
          <div className="p-4 border-b space-y-4">
            <div className="flex items-center justify-between">
              <h1 className="text-xl font-semibold tracking-tight">Inbox</h1>
              <Badge variant="secondary" className="rounded-full bg-green-600/10 text-green-600 hover:bg-green-600/20 border-transparent font-normal shadow-none">
                {totalCount} messages
              </Badge>
            </div>
            <div className="relative">
              <Input
                placeholder="Search..."
                value={searchKeyword}
                onChange={(e) => handleSearchChange(e.target.value)}
                className="h-9 pl-9 bg-background"
              />
              <div className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <circle cx="11" cy="11" r="8" />
                  <path d="m21 21-4.3-4.3" />
                </svg>
              </div>
              {searchKeyword && (
                <button
                  onClick={handleResetSearch}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                >
                  <IconX size={14} />
                </button>
              )}
            </div>
          </div>

          {/* List Content */}
          <div className="flex-1 overflow-y-auto">
            {loading && data.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-32 text-muted-foreground">
                <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-primary mb-2"></div>
                <span className="text-sm">Loading...</span>
              </div>
            ) : data.length === 0 ? (
              <div className="flex flex-col items-center justify-center h-32 text-muted-foreground p-4 text-center">
                <IconBell className="h-8 w-8 mb-2 opacity-20" />
                <p className="text-sm">No notifications found</p>
              </div>
            ) : (
              <div className="divide-y divide-border/50">
                {groupedData.map((group) => (
                  <div key={group.label}>
                    <div className="px-4 py-2 bg-muted/20 text-xs font-medium text-muted-foreground uppercase tracking-wider sticky top-0 backdrop-blur-sm">
                      {group.label}
                    </div>
                    {group.items.map((item) => (
                      <div
                        key={item.id}
                        onClick={() => {
                          setSelectedId(item.id)
                          if (item.status === "UNREAD") {
                            markAsRead(item.id)
                          }
                        }}
                        className={cn(
                          "relative p-4 cursor-pointer transition-colors hover:bg-muted/50",
                          selectedId === item.id ? "bg-primary/5 border-l-2 border-l-primary" : "border-l-2 border-l-transparent",
                          item.status === "UNREAD" ? "bg-background" : "bg-muted/5"
                        )}
                      >
                        <div className="flex justify-between items-start mb-1">
                          <h3 className={cn(
                            "text-sm font-medium truncate pr-2",
                            item.status === "UNREAD" ? "text-foreground" : "text-muted-foreground"
                          )}>
                            {item.sender || 'System'}
                          </h3>
                          <span className={cn(
                            "text-xs whitespace-nowrap shrink-0",
                            item.status === "UNREAD" ? "text-primary" : "text-muted-foreground"
                          )}>
                            {dayjs(item.createdAt).fromNow(true)}
                          </span>
                        </div>
                        <h4 className={cn(
                          "text-sm mb-1 truncate",
                          item.status === "UNREAD" ? "font-medium text-foreground" : "text-muted-foreground"
                        )}>
                          {item.title}
                        </h4>
                        <p className="text-xs text-muted-foreground line-clamp-2">
                          {item.content}
                        </p>
                        {item.status === "UNREAD" && (
                          <div className="absolute right-4 top-1/2 -translate-y-1/2 w-2 h-2 rounded-full bg-primary" />
                        )}
                      </div>
                    ))}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Pagination Footer */}
          {renderPagination()}
        </div>

        {/* Right Detail Pane */}
        <div className="flex-1 flex flex-col min-w-0 bg-background h-full overflow-hidden">
          {selectedItem ? (
            <div className="flex flex-col h-full animate-in fade-in duration-300">
              {/* Detail Header */}
              <div className="p-6 border-b shrink-0 flex items-start justify-between gap-4 h-[113px]">
                <div className="min-w-0 flex-1">
                  <h2 className="text-xl font-semibold mb-4 text-foreground line-clamp-1">
                    {selectedItem.title}
                  </h2>
                  <div className="flex items-center gap-3 text-sm text-muted-foreground">
                    <div className={cn(
                      "flex items-center justify-center w-8 h-8 rounded-full shrink-0",
                      (!selectedItem.sender || selectedItem.sender === "SYSTEM") ? "bg-blue-500/10 text-blue-500" : "bg-green-500/10 text-green-500"
                    )}>
                      {(!selectedItem.sender || selectedItem.sender === "SYSTEM") ? <IconBell size={16} /> : <IconMail size={16} />}
                    </div>
                    <div className="flex flex-col">
                      <span className="font-medium text-foreground">
                        {selectedItem.sender || 'System'}
                        {selectedItem.senderEmail && <span className="font-normal text-muted-foreground ml-1">&lt;{selectedItem.senderEmail}&gt;</span>}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="flex flex-col justify-end h-full shrink-0 items-end gap-2">
                  <UserDateTime
                    value={selectedItem.createdAt}
                    className="text-sm text-muted-foreground"
                  />
                </div>
              </div>

              {/* Detail Content */}
              <div className="flex-1 overflow-y-auto p-8">
                <div className="prose prose-sm max-w-none text-foreground/90 leading-relaxed whitespace-pre-wrap">
                  {selectedItem.content}
                </div>
              </div>
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center h-full text-muted-foreground bg-muted/5">
              <div className="size-16 rounded-full bg-muted flex items-center justify-center mb-4">
                <IconMail className="size-8 opacity-40" />
              </div>
              <h3 className="text-lg font-medium text-foreground mb-1">No message selected</h3>
              <p className="text-sm max-w-xs text-center">
                Choose a message from the list on the left to view its details.
              </p>
            </div>
          )}
        </div>
      </div>
    </LayoutBody>
  )
}
