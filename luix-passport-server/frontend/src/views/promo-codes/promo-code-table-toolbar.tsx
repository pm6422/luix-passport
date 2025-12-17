import { useState, useRef, useCallback } from "react"
import { IconPlus, IconX } from "@tabler/icons-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { GenerateDialog } from "./generate-dialog"
import { GeneratePromoCode } from "@/domains/promo-code"
import { DataTableFacetedFilter } from "@/components/custom/data-table/data-table-faceted-filter"
import { yesNoOptions } from "@/data/yes-no-options"

interface DataTableToolbarProps {
  entityName: string,
  loadPage: (pageNo: number | undefined, pageSize: number | undefined, sorts: Array<string> | undefined, criteria: any) => void,
  save: (formData: GeneratePromoCode) => Promise<void>
}

interface FilterState {
  batchId: string
  enabled: string[] | undefined
}

const initialFilters: FilterState = {
  batchId: "",
  enabled: undefined
}

export function DataTableToolbar({
  entityName,
  loadPage,
  save
}: DataTableToolbarProps) {
  const [filters, setFilters] = useState<FilterState>(initialFilters)
  const debounceRef = useRef<NodeJS.Timeout | null>(null)

  const triggerSearch = useCallback((newFilters: FilterState) => {
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }

    debounceRef.current = setTimeout(() => {
      loadPage(undefined, undefined, undefined, {
        batchId: newFilters.batchId || undefined,
        enabled: (newFilters.enabled && newFilters.enabled.length === 1) ? newFilters.enabled[0] : undefined
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

  const isFiltered = filters.batchId || (filters.enabled && filters.enabled.length > 0)

  return (
    <div className="flex items-center justify-between w-full">
      <div className="flex flex-1 items-center gap-2 flex-wrap">
        <Input
          placeholder="Filter Batch ID..."
          value={filters.batchId}
          onChange={(e) => handleFilterChange("batchId", e.target.value)}
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
        <GenerateDialog entityName={entityName} save={save}>
          <Button variant="secondary" size="sm">
            <IconPlus className="me-2 size-4" aria-hidden="true" />
            Generate Codes
          </Button>
        </GenerateDialog>
      </div>
    </div>
  )
}
