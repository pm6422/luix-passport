import { ColumnDef } from "@tanstack/react-table"
import { Checkbox } from "@/components/ui/checkbox"
import { IconEdit } from "@tabler/icons-react"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { DataTableRowActions } from "@/components/custom/data-table/data-table-row-actions"
import { Button } from "@/components/ui/button"
import { YesNoBadge } from "@/components/custom/display/yes-no-badge"
import { DataDict } from "@/domains/data-dict"
import { EditDialog } from "../dialog/data-dict-dialog"
import { UserDateTime } from "@/components/custom/display/user-date-time"

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
      accessorKey: "id",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="ID" />
      ),
      cell: ({ row }) => <div className="w-[100px]">{row.original.id}</div>,
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
            <YesNoBadge value={row.original.enabled} />
          </div>
        )
      },
      // filterFn: (row, id, value) => {
      //   return value.includes(row.getValue(id))
      // },
    },
    {
      accessorKey: "updatedAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Updated At" />
      ),
      cell: ({ row }) => <div className="w-[100px]"><UserDateTime value={row.original.updatedAt} twoLines={true} /></div>,
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
