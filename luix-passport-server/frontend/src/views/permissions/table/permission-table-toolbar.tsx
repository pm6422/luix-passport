import { useState, useRef, useCallback } from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { IconPlus, IconX } from "@tabler/icons-react"
import { EditDialog } from "../dialog/permission-dialog"
import { type Permission, type PermissionCriteriaSchema } from "@/domains/permission"

interface DataTableToolbarProps{
  entityName: string,
  loadPage: (pageNo: number | undefined, pageSize: number | undefined, sorts: Array<string> | undefined, criteria: PermissionCriteriaSchema) => void,
  save: (formData: Permission) => Promise<void>
}

interface FilterState {
  resourceType: string
  action: string
}

const initialFilters: FilterState = {
  resourceType: "",
  action: ""
}

export function DataTableToolbar ({
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
        resourceType: newFilters.resourceType || undefined,
        action: newFilters.action || undefined
      })
    }, 500)
  }, [loadPage])

  const handleFilterChange = (key: keyof FilterState, value: string) => {
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

  const isFiltered = filters.resourceType || filters.action

  return (
    <div className="flex items-center justify-between w-full">
      <div className="flex flex-1 items-center gap-2 flex-wrap">
        <Input
          placeholder="Filter resource type..."
          value={filters.resourceType}
          onChange={(e) => handleFilterChange("resourceType", e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <Input
          placeholder="Filter action..."
          value={filters.action}
          onChange={(e) => handleFilterChange("action", e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
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
