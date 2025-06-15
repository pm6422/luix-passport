import { ColumnDef } from "@tanstack/react-table"
import { Checkbox } from "@/components/ui/checkbox"
import { IconEdit } from "@tabler/icons-react"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { DataTableRowActions } from "@/components/custom/data-table/data-table-row-actions"
import { Button } from "@/components/ui/button"
import { StatusBadge } from "@/components/custom/status-badge"
import { DataDict } from "@/domains/data-dict"
import { EditDialog } from "../dialog/edit-dialog"
import { DateTime } from "@/components/custom/date-time"

export function tableColumns(
  entityName: string,
  save: (formData: DataDict) => Promise<void>,
  deleteRow: (row: DataDict) => Promise<void>
): ColumnDef<DataDict>[] {

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
      accessorKey: "num",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Number" />
      ),
      cell: ({ row }) => <div className="w-[30px]">{row.original.num}</div>,
      enableSorting: true,
      enableHiding: false,
    },
    {
      accessorKey: "categoryCode",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Category Code" />
      ),
      cell: ({ row }) => <div className="w-[175px]">{row.original.categoryCode}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "dictCode",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Dictionary Code" />
      ),
      cell: ({ row }) => <div className="w-[150px]">{row.original.dictCode}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "dictName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Dictionary Name" />
      ),
      cell: ({ row }) => <div className="w-[50px]">{row.original.dictName}</div>,
      enableSorting: true,
      enableHiding: true,
    },
    {
      accessorKey: "enabled",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Enabled" />
      ),
      cell: ({ row }) => {
        return (
          <div className="flex w-[50px] items-center justify-center">
            <StatusBadge value={row.original.enabled} />
          </div>
        )
      },
      filterFn: (row, id, value) => {
        return value.includes(row.getValue(id))
      },
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
        <DataTableRowActions<DataDict> entityName={entityName} row={row} deleteRow={deleteRow}>
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
