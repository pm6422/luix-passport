import { ColumnDef } from "@tanstack/react-table"
import { PromoCode } from "@/domains/promo-code"
import { DataTableColumnHeader } from "@/components/custom/data-table/data-table-column-header"
import { Badge } from "@/components/ui/badge"
import dayjs from "dayjs"

export const columns: ColumnDef<PromoCode>[] = [
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
      <DataTableColumnHeader column={column} title="Status" />
    ),
    cell: ({ row }) => {
      const enabled = row.getValue("enabled")
      return (
        <Badge variant={enabled ? "default" : "secondary"}>
          {enabled ? "Enabled" : "Disabled"}
        </Badge>
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
      const date = row.getValue("usedAt")
      if (!date) return <div>-</div>
      return <div>{dayjs(date as string).format("YYYY-MM-DD HH:mm")}</div>
    },
    enableSorting: false,
  },
  {
    accessorKey: "expiresAt",
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Expires At" />
    ),
    cell: ({ row }) => {
      const date = row.getValue("expiresAt")
      if (!date) return <div>-</div>
      return <div>{dayjs(date as string).format("YYYY-MM-DD HH:mm")}</div>
    },
    enableSorting: false,
  },
]
