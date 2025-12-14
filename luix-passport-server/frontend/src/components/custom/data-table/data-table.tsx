import React, { useState, useEffect } from "react"
import {
  ColumnDef,
  PaginationState,
  SortingState,
  ColumnFiltersState,
  VisibilityState,
  flexRender,
  getCoreRowModel,
  getFacetedRowModel,
  getFacetedUniqueValues,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable
} from "@tanstack/react-table"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { DataTablePagination } from "./data-table-pagination"
import { DataTableViewOptions } from "@/components/custom/data-table/data-table-view-options"
import { parseSorts } from "@/lib/table-utils"
import { Button } from "@/components/ui/button"
import { IconTrash } from "@tabler/icons-react"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"
import { MinusIntroY } from "@/components/custom/display/intro-animation"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { BaseCriteria } from "@/domains/base/base-criteria"

// Server pagination props
interface ServerPaginationProps<TData> {
  paginationMode: "server"
  totalCount: number
  totalPages: number
  loadPage: (
    pageNo: number,
    pageSize: number,
    sorts?: string[],
    criteria?: BaseCriteria
  ) => void
  deleteRows?: (rows: TData[]) => Promise<void[]>
}

// Client pagination props
interface ClientPaginationProps<TData> {
  paginationMode: "client"
  loadPage: (criteria?: BaseCriteria) => void
  deleteRows?: (rows: TData[]) => Promise<void[]>
}

// Common props
interface BaseDataTableProps<TData, TValue> {
  children: React.ReactNode
  columns: ColumnDef<TData, TValue>[]
  data: TData[]
}

// Union type for all props
type DataTableProps<TData, TValue> = BaseDataTableProps<TData, TValue> &
  (ServerPaginationProps<TData> | ClientPaginationProps<TData>)

export function DataTable<TData, TValue>(
  props: DataTableProps<TData, TValue>
) {
  const { children, columns, data, paginationMode, deleteRows } = props

  const [rowSelection, setRowSelection] = useState({})
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
  const [sorting, setSorting] = useState<SortingState>([])
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([])
  const [delConfirmPopoverOpen, setDelConfirmPopoverOpen] = useState(false)

  // Server pagination state
  const [pagination, setPagination] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10
  })

  const isServerPagination = paginationMode === "server"

  const table = useReactTable({
    data,
    columns,
    state: {
      columnVisibility,
      rowSelection,
      sorting,
      columnFilters,
      ...(isServerPagination && { pagination }),
    },
    // Server pagination configuration
    ...(isServerPagination && {
      manualPagination: true,
      manualSorting: true,
      manualFiltering: true,
      pageCount: (props as ServerPaginationProps<TData>).totalPages,
      onPaginationChange: setPagination,
    }),
    // Client pagination configuration
    ...(!isServerPagination && {
      getPaginationRowModel: getPaginationRowModel(),
    }),
    // Common configuration
    enableRowSelection: true,
    onRowSelectionChange: setRowSelection,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    onColumnVisibilityChange: setColumnVisibility,
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFacetedRowModel: getFacetedRowModel(),
    getFacetedUniqueValues: getFacetedUniqueValues(),
  })

  // Server pagination effect
  useEffect(() => {
    if (isServerPagination) {
      const serverProps = props as ServerPaginationProps<TData>
      if (serverProps.loadPage) {
        serverProps.loadPage(
          pagination.pageIndex,
          pagination.pageSize,
          parseSorts(sorting)
        )
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagination, sorting, isServerPagination])

  const handleDelete = () => {
    const selectedRows = Object.keys(rowSelection).map(rowIndex =>
      data[parseInt(rowIndex)]
    )

    if (deleteRows) {
      toast.promise(deleteRows(selectedRows), {
        loading: "Deleting rows...",
        success: () => {
          setRowSelection({})
          // Reload data after deletion
          if (!isServerPagination) {
            const clientProps = props as ClientPaginationProps<TData>
            clientProps.loadPage()
          }
          // Server pagination will auto-reload via useEffect
          return "Deleted selected rows"
        },
        error: (error) => {
          return getErrorMessage(error)
        }
      })
    }
  }

  const totalCount = isServerPagination
    ? (props as ServerPaginationProps<TData>).totalCount
    : data.length

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between space-x-2">
        {children}
        {Object.keys(rowSelection).length > 0 && (
          <Popover open={delConfirmPopoverOpen} onOpenChange={setDelConfirmPopoverOpen}>
            <PopoverTrigger asChild>
              <Button
                variant="destructive"
                size="sm"
                className="hidden h-8 lg:flex"
              >
                <IconTrash className="mr-2 h-4 w-4" />
                Delete{`(${Object.keys(rowSelection).length})`}
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-[240px]">
              Are your sure to delete?
              <div className="mt-4 flex items-center justify-between space-x-2">
                <Button
                  className="w-full"
                  variant="secondary"
                  size="sm"
                  onClick={() => setDelConfirmPopoverOpen(false)}
                >
                  No
                </Button>
                <Button
                  className="w-full"
                  variant="destructive"
                  size="sm"
                  onClick={handleDelete}
                >
                  Yes
                </Button>
              </div>
            </PopoverContent>
          </Popover>
        )}
        <DataTableViewOptions columns={table.getAllColumns()} />
      </div>
      <div className="overflow-hidden rounded-lg border">
        <MinusIntroY>
          <Table>
            <TableHeader className="bg-muted">
              {table.getHeaderGroups().map((headerGroup) => (
                <TableRow key={headerGroup.id}>
                  {headerGroup.headers.map((header) => {
                    return (
                      <TableHead key={header.id} colSpan={header.colSpan}>
                        {header.isPlaceholder
                          ? null
                          : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                      </TableHead>
                    )
                  })}
                </TableRow>
              ))}
            </TableHeader>
            <TableBody>
              {table.getRowModel().rows?.length ? (
                table.getRowModel().rows.map((row) => (
                  <TableRow
                    key={row.id}
                    data-state={row.getIsSelected() && "selected"}
                    className="border-b last:border-0"
                  >
                    {row.getVisibleCells().map((cell) => (
                      <TableCell key={cell.id}>
                        {flexRender(
                          cell.column.columnDef.cell,
                          cell.getContext()
                        )}
                      </TableCell>
                    ))}
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell
                    colSpan={columns.length}
                    className="h-24 text-center"
                  >
                    No results.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </MinusIntroY>
      </div>
      <DataTablePagination table={table} totalCount={totalCount} />
    </div>
  )
}
