import { useState, useRef, useCallback } from "react"
import { IconPlus, IconX } from "@tabler/icons-react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

import { EditDialog } from "../dialog/user-dialog"
import { type User, type UserCriteriaSchema } from "@/domains/user"
import { yesNoOptions } from "@/data/yes-no-options"
import { DataTableFacetedFilter } from "@/components/custom/data-table/data-table-faceted-filter"

// --- DataTableToolbar Component ---

interface DataTableToolbarProps {
  entityName: string,
  loadPage: (pageNo: number | undefined, pageSize: number | undefined, sorts: Array<string> | undefined, criteria: UserCriteriaSchema) => void,
  save: (formData: User) => Promise<void>
}

// Define the filter state type
interface FilterState {
  username: string
  email: string
  mobileNo: string
  enabled: string[] | undefined
}

const initialFilters: FilterState = {
  username: "",
  email: "",
  mobileNo: "",
  enabled: undefined
}

export function DataTableToolbar({
  entityName,
  loadPage,
  save
}: DataTableToolbarProps) {
  const [filters, setFilters] = useState<FilterState>(initialFilters)

  // Debounce timer ref
  const debounceRef = useRef<NodeJS.Timeout | null>(null)

  const triggerSearch = useCallback((newFilters: FilterState) => {
    // Clear previous timer
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }

    // Set new timer
    debounceRef.current = setTimeout(() => {
      loadPage(undefined, undefined, undefined, {
        username: newFilters.username || undefined,
        email: newFilters.email || undefined,
        mobileNo: newFilters.mobileNo || undefined,
        enabled: newFilters.enabled?.[0] || undefined
      })
    }, 500)
  }, [loadPage])

  const handleFilterChange = (key: keyof FilterState, value: any) => {
    const newFilters = { ...filters, [key]: value }
    setFilters(newFilters)
    triggerSearch(newFilters)
  }

  const handleReset = () => {
    setFilters(initialFilters)
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }
    loadPage(undefined, undefined, undefined, {})
  }

  const isFiltered = filters.username || filters.email || filters.mobileNo || (filters.enabled && filters.enabled.length > 0)

  return (
    <div className="flex items-center justify-between w-full">
      <div className="flex flex-1 items-center gap-2 flex-wrap">
        <Input
          placeholder="Filter username..."
          value={filters.username}
          onChange={(e) => handleFilterChange("username", e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <Input
          placeholder="Filter email..."
          value={filters.email}
          onChange={(e) => handleFilterChange("email", e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <Input
          placeholder="Filter mobile..."
          value={filters.mobileNo}
          onChange={(e) => handleFilterChange("mobileNo", e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <DataTableFacetedFilter
          title="Enabled"
          options={yesNoOptions}
          value={filters.enabled}
          onChange={(val) => handleFilterChange("enabled", val)}
        />

        {isFiltered && (
          <Button
            variant="ghost"
            size="sm"
            onClick={handleReset}
          >
            Reset
            <IconX className="ml-2 h-4 w-4" />
          </Button>
        )}
      </div>
      <div className="gap-2 flex">
        <EditDialog entityName={entityName} save={save}>
          <Button variant="secondary" size="sm">
            <IconPlus className="me-2 size-4" aria-hidden="true" />
            Create
          </Button>
        </EditDialog>
      </div>
    </div>
  )
}
