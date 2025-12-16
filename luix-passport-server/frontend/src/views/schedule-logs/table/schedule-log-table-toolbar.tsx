import { useState, useRef, useCallback } from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { IconX } from "@tabler/icons-react"
import { type ScheduleLogCriteriaSchema } from "@/domains/schedule-log"
import { DataTableFacetedFilter } from "@/components/custom/data-table/data-table-faceted-filter"

interface DataTableToolbarProps {
  loadPage: (pageNo: number | undefined, pageSize: number | undefined, sorts: Array<string> | undefined, criteria: ScheduleLogCriteriaSchema) => void
}

interface FilterState {
  scheduleName: string
  status: string[] | undefined
}

const initialFilters: FilterState = {
  scheduleName: "",
  status: undefined
}

const statusOptions = [
  { value: "SUCCESS", label: "SUCCESS" },
  { value: "FAILURE", label: "FAILURE" },
  { value: "RUNNING", label: "RUNNING" }
]

export function DataTableToolbar({
  loadPage
}: DataTableToolbarProps) {
  const [filters, setFilters] = useState<FilterState>(initialFilters)
  const debounceRef = useRef<NodeJS.Timeout | null>(null)

  const triggerSearch = useCallback((newFilters: FilterState) => {
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }

    debounceRef.current = setTimeout(() => {
      loadPage(undefined, undefined, undefined, {
        scheduleName: newFilters.scheduleName || undefined,
        status: newFilters.status?.length ? newFilters.status[0] as "SUCCESS" | "FAILURE" | "RUNNING" : undefined
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

  const isFiltered = filters.scheduleName || (filters.status && filters.status.length > 0)

  return (
    <div className="flex items-center justify-between w-full">
      <div className="flex flex-1 items-center gap-2 flex-wrap">
        <Input
          placeholder="Filter Schedule Name..."
          value={filters.scheduleName}
          onChange={(e) => handleFilterChange("scheduleName", e.target.value)}
          className="h-8 w-[150px] lg:w-[250px]"
        />
        <DataTableFacetedFilter
          title="Status"
          options={statusOptions}
          value={filters.status}
          onChange={(val) => handleFilterChange("status", val)}
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
    </div>
  )
}
