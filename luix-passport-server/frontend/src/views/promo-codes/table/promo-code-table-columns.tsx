import { ColumnDef } from "@tanstack/react-table"
import { PromoCode } from "@/domains/promo-code"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { Badge } from "@/components/ui/badge"
import { YesNoBadge } from "@/components/custom/display/yes-no-badge"
import { UserDateTime } from "@/components/custom/display/user-date-time"

export function tableColumns(): ColumnDef<PromoCode>[] {
  return [
    {
      accessorKey: "code",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Code" />
    ),
    cell: ({ row }) => <div className="font-mono">{row.getValue("code")}</div>,
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: "type",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Type" />
    ),
    cell: ({ row }) => {
      return (
        <Badge variant="outline">{row.getValue("type")}</Badge>
      )
    },
    enableSorting: false,
  },
  {
    accessorKey: "durationDays",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Duration (Days)" />
    ),
    cell: ({ row }) => <div>{row.getValue("durationDays")}</div>,
    enableSorting: false,
  },
  {
    accessorKey: "batchId",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Batch ID" />
    ),
    cell: ({ row }) => <div>{row.getValue("batchId")}</div>,
    enableSorting: false,
  },
  {
    accessorKey: "enabled",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Enabled" />
    ),
    cell: ({ row }) => {
      return (
        <div className="flex w-[50px] items-center justify-center">
          <YesNoBadge value={row.getValue("enabled")} />
        </div>
      )
    },
    enableSorting: false,
  },
  {
    accessorKey: "usedBy",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Used By" />
    ),
    cell: ({ row }) => <div>{row.getValue("usedBy") || "-"}</div>,
    enableSorting: false,
  },
  {
    accessorKey: "usedAt",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Used At" />
    ),
    cell: ({ row }) => {
      const date = row.getValue("usedAt") as string
      if (!date) return <div>-</div>
      return <div className="w-[100px]"><UserDateTime value={date} twoLines={true}/></div>
    },
    enableSorting: false,
  },
  {
    accessorKey: "expiresAt",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Expires At" />
    ),
    cell: ({ row }) => {
      const date = row.getValue("expiresAt") as string
      if (!date) return <div>-</div>
      return <div className="w-[100px]"><UserDateTime value={date} twoLines={true}/></div>
    },
    enableSorting: false,
  },
]
}
