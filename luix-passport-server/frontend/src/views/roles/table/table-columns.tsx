import { ColumnDef } from "@tanstack/react-table"
import { Checkbox } from "@/components/ui/checkbox"
import { IconEdit } from "@tabler/icons-react"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { DataTableRowActions } from "@/components/custom/data-table/data-table-row-actions"
import { Button } from "@/components/ui/button"
import { Role } from "@/domains/role"
import { EditDialog } from "../dialog/edit-dialog"
import { DateTime } from '@/components/custom/date-time.tsx'

export function tableColumns(
  entityName: string,
  save: (formData: Role) => Promise<void>,
  deleteRow: (row: Role) => Promise<void>,
): ColumnDef<Role>[] {
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
      cell: ({ row }) => <div className="w-[170px]">{row.original.id}</div>,
      enableSorting: true,
      enableHiding: false,
    },
    {
      accessorKey: "remark",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Remark" />
      ),
      cell: ({ row }) => <div className="w-[200px]">{row.original.remark}</div>,
      enableSorting: true,
      enableHiding: false,
    },
    {
      accessorKey: "createdAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Created At" />
      ),
      cell: ({ row }) => <div className="w-[150px]"><DateTime value={row.original.createdAt}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "modifiedAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Modified At" />
      ),
      cell: ({ row }) => <div className="w-[150px]"><DateTime value={row.original.modifiedAt}/></div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      id: "actions",
      cell: ({ row }) => (
        <DataTableRowActions<Role> entityName={entityName} row={row} deleteRow={deleteRow}>
          <EditDialog entityName={entityName} id={row.original.id} save={save}>
            <Button variant="secondary" className="flex size-8 p-0">
              <IconEdit className="size-4" />
              <span className="sr-only">Update</span>
            </Button>
          </EditDialog>
        </DataTableRowActions>
      )
    }
  ]
}
