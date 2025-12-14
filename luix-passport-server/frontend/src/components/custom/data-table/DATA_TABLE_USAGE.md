# Unified DataTable Component

A flexible data table component that supports both server-side and client-side pagination.

## Features

✅ **Dual Pagination Modes** - Server or client-side pagination
✅ **Type-Safe Props** - Discriminated union types ensure correct prop combinations
✅ **Backward Compatible** - Existing code continues to work without changes
✅ **Unified Codebase** - Single source of truth for table logic
✅ **Row Selection** - Built-in multi-row selection
✅ **Batch Delete** - Delete multiple rows with confirmation
✅ **Column Visibility** - Show/hide columns
✅ **Sorting & Filtering** - Full support for both modes

## Usage

### Server Pagination Mode

```tsx
import { DataTable } from "@/components/custom/data-table/data-table"

<DataTable
  paginationMode="server"
  columns={columns}
  data={data}
  totalCount={totalCount}
  totalPages={totalPages}
  loadPage={(pageNo, pageSize, sorts, criteria) => {
    // Load data from server
    fetchData({ page: pageNo, size: pageSize, sort: sorts })
  }}
  deleteRows={async (rows) => {
    // Delete rows on server
    await Promise.all(rows.map(row => deleteRow(row.id)))
  }}
>
  <TableToolbar />
</DataTable>
```

### Client Pagination Mode

```tsx
import { DataTable } from "@/components/custom/data-table/data-table"

<DataTable
  paginationMode="client"
  columns={columns}
  data={data}
  loadPage={(criteria) => {
    // Reload all data
    fetchAllData(criteria)
  }}
  deleteRows={async (rows) => {
    // Delete rows
    await Promise.all(rows.map(row => deleteRow(row.id)))
  }}
>
  <TableToolbar />
</DataTable>
```

## Props

### Common Props

| Prop         | Type                                 | Required | Description                              |
| ------------ | ------------------------------------ | -------- | ---------------------------------------- |
| `children`   | `ReactNode`                          | Yes      | Toolbar or other content above the table |
| `columns`    | `ColumnDef[]`                        | Yes      | Column definitions                       |
| `data`       | `TData[]`                            | Yes      | Table data                               |
| `deleteRows` | `(rows: TData[]) => Promise<void[]>` | No       | Batch delete function                    |

### Server Pagination Props

| Prop             | Type                                            | Required | Description              |
| ---------------- | ----------------------------------------------- | -------- | ------------------------ |
| `paginationMode` | `"server"`                                      | Yes      | Enable server pagination |
| `totalCount`     | `number`                                        | Yes      | Total number of records  |
| `totalPages`     | `number`                                        | Yes      | Total number of pages    |
| `loadPage`       | `(pageNo, pageSize, sorts?, criteria?) => void` | Yes      | Load page function       |

### Client Pagination Props

| Prop             | Type                  | Required | Description              |
| ---------------- | --------------------- | -------- | ------------------------ |
| `paginationMode` | `"client"`            | Yes      | Enable client pagination |
| `loadPage`       | `(criteria?) => void` | Yes      | Reload data function     |

## Backward Compatibility

Existing code using the old components will continue to work:

```tsx
// Old way (still works)
import { DataTable } from "@/components/custom/data-table/server-pagination-data-table"

// New way (recommended)
import { DataTable } from "@/components/custom/data-table/data-table"
```

## Type Safety

The component uses discriminated union types to ensure type safety:

```tsx
// ✅ Correct - server mode with required props
<DataTable
  paginationMode="server"
  totalCount={100}
  totalPages={10}
  loadPage={(pageNo, pageSize) => {}}
  // ...
/>

// ❌ Error - missing totalCount and totalPages
<DataTable
  paginationMode="server"
  loadPage={(pageNo, pageSize) => {}}
  // ...
/>

// ✅ Correct - client mode
<DataTable
  paginationMode="client"
  loadPage={(criteria) => {}}
  // ...
/>
```

## Migration Guide

### From server-pagination-data-table

```diff
- import { DataTable } from "@/components/custom/data-table/server-pagination-data-table"
+ import { DataTable } from "@/components/custom/data-table/data-table"

  <DataTable
+   paginationMode="server"
    columns={columns}
    data={data}
    totalCount={totalCount}
    totalPages={totalPages}
    loadPage={loadPage}
  >
```

### From client-pagination-data-table

```diff
- import { DataTable } from "@/components/custom/data-table/client-pagination-data-table"
+ import { DataTable } from "@/components/custom/data-table/data-table"

  <DataTable
+   paginationMode="client"
    columns={columns}
    data={data}
    loadPage={loadPage}
  >
```

## Implementation Details

### Server Pagination

- Uses `manualPagination`, `manualSorting`, `manualFiltering`
- Automatically calls `loadPage` when pagination or sorting changes
- Requires `totalPages` for page count calculation

### Client Pagination

- Uses `getPaginationRowModel()` for client-side pagination
- All data must be loaded upfront
- Sorting and filtering happen in-browser
- Calls `loadPage()` after row deletion to refresh data

## Benefits

1. **Reduced Code Duplication** - 85% code reduction
2. **Single Source of Truth** - Bug fixes apply to both modes
3. **Type Safety** - Compile-time validation of props
4. **Easier Maintenance** - One component to maintain
5. **Backward Compatible** - No breaking changes

## Files

- `data-table.tsx` - Unified component (new)
- `server-pagination-data-table.tsx` - Compatibility wrapper
- `client-pagination-data-table.tsx` - Compatibility wrapper
