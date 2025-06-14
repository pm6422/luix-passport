import { ColumnDef } from "@tanstack/react-table"
import { Checkbox } from "@/components/ui/checkbox"
import { IconEdit } from "@tabler/icons-react"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { DataTableRowActions } from "@/components/custom/data-table/data-table-row-actions"
import { Button } from "@/components/ui/button"
import { Schedule } from "@/domains/schedule"
import { EditDialog } from "../dialog/edit-dialog"
import { DateTime } from "@/components/custom/date-time"

export function tableColumns(
  entityName: string,
  deleteRow: (row: Schedule) => Promise<void>
): ColumnDef<Schedule>[] {

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
      cell: ({ row }) => <div className="w-[150px]">{row.getValue("id")}</div>,
      enableSorting: true,
      enableHiding: false,
    },
    {
      accessorKey: "lockUntil",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Lock Until" />
      ),
      cell: ({ row }) => <div className="w-[150px]"><DateTime value={row.getValue("lockUntil")}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "lockedAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Locked At" />
      ),
      cell: ({ row }) => <div className="w-[150px]"><DateTime value={row.getValue("lockedAt")}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "lockedBy",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Locked By" />
      ),
      cell: ({ row }) => <div className="w-[75px]">{row.getValue("lockedBy")}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      id: "actions",
      cell: ({ row }) => (
        <DataTableRowActions<Schedule> entityName={entityName} row={row} deleteRow={deleteRow}
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