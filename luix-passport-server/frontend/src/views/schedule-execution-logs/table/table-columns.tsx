import { ColumnDef } from "@tanstack/react-table"
import { Checkbox } from "@/components/ui/checkbox"
import { IconEdit } from "@tabler/icons-react"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { DataTableRowActions } from "@/components/custom/data-table/data-table-row-actions"
import { Button } from "@/components/ui/button"
import { ScheduleExecutionLog } from "@/domains/schedule-execution-log.ts"
import { EditDialog } from "../dialog/edit-dialog"
import { DateTime } from "@/components/custom/date-time"

export function tableColumns(
  entityName: string,
  deleteRow: (row: ScheduleExecutionLog) => Promise<void>
): ColumnDef<ScheduleExecutionLog>[] {

  return [
    {
      id: "select",
      header: ({ table }) => (
        <Checkbox
          checked={
            table.getIsAllPageRowsSelected() ||
            (table.getIsSomePageRowsSelected() && "indeterminate")
          }
          onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
          aria-label="Select all"
          className="translate-y-[2px]"
        />
      ),
      cell: ({ row }) => (
        <Checkbox
          checked={row.getIsSelected()}
          onCheckedChange={(value) => row.toggleSelected(!!value)}
          aria-label="Select row"
          className="translate-y-[2px]"
        />
      ),
      enableSorting: false,
      enableHiding: false,
    },
    {
      accessorKey: "id",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="ID" />
      ),
      cell: ({ row }) => <div className="w-[100px]">{row.original.id}</div>,
      enableSorting: true,
      enableHiding: false,
    },
    {
      accessorKey: "scheduleName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Schedule Name" />
      ),
      cell: ({ row }) => <div className="w-[175px]">{row.original.scheduleName}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "startAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Start At" />
      ),
      cell: ({ row }) => <div className="w-[75px]"><DateTime value={row.original.startAt}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "endAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="End At" />
      ),
      cell: ({ row }) => <div className="w-[75px]"><DateTime value={row.original.endAt}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "status",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Status" />
      ),
      cell: ({ row }) => <div className="w-[50px]">{row.original.status}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "node",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Node" />
      ),
      cell: ({ row }) => <div className="w-[75px]">{row.original.node}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      id: "actions",
      cell: ({ row }) => (
        <DataTableRowActions<ScheduleExecutionLog> entityName={entityName} row={row} deleteRow={deleteRow}
                                                   children={
            <EditDialog entityName={entityName} id={row.original.id}>
              <Button variant="secondary" className="flex size-8 p-0">
                <IconEdit className="size-4" />
                <span className="sr-only">Update</span>
              </Button>
            </EditDialog>
          }
        >
        </DataTableRowActions>
      )
    }
  ]
}