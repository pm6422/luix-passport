import { useState, useRef, useCallback } from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { IconPlus, IconUpload, IconX } from "@tabler/icons-react"
import { EditDialog } from "../dialog/data-dict-dialog"
import { type DataDict, type DataDictCriteria } from "@/domains/data-dict"
import { UploadDialog } from "@/components/custom/dialog/upload-dialog"
import { yesNoOptions } from "@/data/yes-no-options"
import { UserTableFacetedFilter } from "../../users/table/user-table-faceted-filter"

type UploadFormSchema = { files: File[] }

interface DataTableToolbarProps {
  entityName: string,
  loadPage: (pageNo: number | undefined, pageSize: number | undefined, sorts: Array<string> | undefined, criteria: DataDictCriteria) => void,
  save: (formData: DataDict) => Promise<void>,
  upload: (formData: UploadFormSchema) => Promise<Array<void>>
}

interface FilterState {
  id: string
  categoryCode: string
  enabled: string[] | undefined
}

const initialFilters: FilterState = {
  id: "",
  categoryCode: "",
  enabled: undefined
}

export function DataTableToolbar({
  entityName,
  loadPage,
  save,
  upload
}: DataTableToolbarProps) {
  const [filters, setFilters] = useState<FilterState>(initialFilters)
  const debounceRef = useRef<NodeJS.Timeout | null>(null)

  const triggerSearch = useCallback((newFilters: FilterState) => {
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }

    debounceRef.current = setTimeout(() => {
      loadPage(undefined, undefined, undefined, {
        id: newFilters.id || undefined,
        categoryCode: newFilters.categoryCode || undefined,
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

  const isFiltered = filters.id || filters.categoryCode || (filters.enabled && filters.enabled.length > 0)

  return (
    <div className="flex items-center justify-between w-full">
      <div className="flex flex-1 items-center gap-2 flex-wrap">
        <Input
          placeholder="Filter ID..."
          value={filters.id}
          onChange={(e) => handleFilterChange("id", e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <Input
          placeholder="Filter Category Code..."
          value={filters.categoryCode}
          onChange={(e) => handleFilterChange("categoryCode", e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <UserTableFacetedFilter
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
        <UploadDialog entityName={entityName} upload={upload} description="Supported file types: .txt, .json, you can use downloaded the template." templateUrl="/api/data-dicts/import-template">
          <Button variant="secondary" size="sm">
            <IconUpload className="me-2 size-4" aria-hidden="true" />
            Upload
          </Button>
        </UploadDialog>
      </div>
    </div>
  )
}
