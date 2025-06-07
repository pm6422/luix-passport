import { useState } from "react"
import {
  ColumnDef,
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
  useReactTable,
} from "@tanstack/react-table"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { DataTablePagination } from "./data-table-pagination"
import { DataTableViewOptions } from "@/components/custom/data-table/data-table-view-options"
import { Button } from "@/components/custom/button"
import { IconTrash } from "@tabler/icons-react"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"
import { MinusIntroY } from "@/components/custom/intro-motion"

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"

interface DataTableProps<TData, TValue> {
  children: React.ReactNode,
  columns: ColumnDef<TData, TValue>[]
  data: TData[],
  loadPage: Function,
  deleteRows?: (rows: Array<any>) => Promise<any>
}

export function DataTable<TData, TValue>({
  children,
  columns,
  data,
  loadPage,
  deleteRows
}: DataTableProps<TData, TValue>) {
  const [rowSelection, setRowSelection] = useState({})
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
  const [sorting, setSorting] = useState<SortingState>([])
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([])
  const [delConfirmPopoverOpen, setDelConfirmPopoverOpen] = useState(false)

  const table = useReactTable({
    data,
    columns,
    state: {
      columnVisibility,
      rowSelection,
      sorting,
      columnFilters,
    },
    enableRowSelection: true,
    onRowSelectionChange: setRowSelection,
    onSortingChange: setSorting,
    onColumnFiltersChange: setColumnFilters,
    onColumnVisibilityChange: setColumnVisibility,
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFacetedRowModel: getFacetedRowModel(),
    getFacetedUniqueValues: getFacetedUniqueValues(),
  })

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between space-x-2">
        {children}
        { Object.keys(rowSelection).length > 0 && (
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
                onClick={() => {
                  const selectedRows = Object.keys(rowSelection).map(rowIndex => data[parseInt(rowIndex)]);
                  if(deleteRows) {
                    toast.promise(deleteRows(selectedRows), {
                      loading: "Deleting rows...",
                      success: () => {
                        setRowSelection({})
                        loadPage()
                        return "Deleted selected rows"
                      },
                      error: (error) => {
                        return getErrorMessage(error)
                      }
                    })
                  }
                }}
              >
                Yes
              </Button>
            </div>
          </PopoverContent>
        </Popover>
        )}
        <DataTableViewOptions columns={table.getAllColumns()} />
      </div>
      <div className="rounded-md border">
        <MinusIntroY>
          <Table>
            <TableHeader>
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
                    className="border-b border-dashed last:border-0"
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
      <DataTablePagination table={table} totalCount={data.length}/>
    </div>
  )
}
