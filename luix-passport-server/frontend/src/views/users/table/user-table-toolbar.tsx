import { useState, useRef } from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { IconPlus, IconX } from "@tabler/icons-react"
import { EditDialog } from "../dialog/user-dialog"
import { type User, type UserCriteriaSchema } from "@/domains/user"
import { DataTableFacetedFilter } from "./data-table-faceted-filter"
import { Column } from "@tanstack/react-table"
import { yesNoOptions } from "@/data/yes-no-options"

interface DataTableToolbarProps {
  entityName: string,
  loadPage: (pageNo: number | undefined, pageSize: number | undefined, sorts: Array<string> | undefined, criteria: UserCriteriaSchema) => void,
  save: (formData: User) => Promise<void>
}

// Helper to create a fake column object for DataTableFacetedFilter
function createFakeColumn(
  id: string, 
  value: any, 
  setValue: (val: any) => void
): Column<any, any> {
  return {
    id,
    getFilterValue: () => value,
    setFilterValue: setValue,
    getFacetedUniqueValues: () => new Map(),
  } as unknown as Column<any, any>
}

export function DataTableToolbar({
  entityName,
  loadPage,
  save
}: DataTableToolbarProps) {
  const [username, setUsername] = useState("")
  const [email, setEmail] = useState("")
  const [mobileNo, setMobileNo] = useState("")
  const [enabled, setEnabled] = useState<string[] | undefined>(undefined)

  // Debounce timer ref
  const debounceRef = useRef<NodeJS.Timeout | null>(null)

  const triggerSearch = (
    newUsername: string,
    newEmail: string,
    newMobileNo: string,
    newEnabled: string[] | undefined
  ) => {
    // Clear previous timer
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }

    // Set new timer
    debounceRef.current = setTimeout(() => {
      loadPage(undefined, undefined, undefined, {
        username: newUsername || undefined,
        email: newEmail || undefined,
        mobileNo: newMobileNo || undefined,
        enabled: newEnabled && newEnabled.length > 0 ? newEnabled[0] : undefined
      })
    }, 500)
  }

  const handleUsernameChange = (value: string) => {
    setUsername(value)
    triggerSearch(value, email, mobileNo, enabled)
  }

  const handleEmailChange = (value: string) => {
    setEmail(value)
    triggerSearch(username, value, mobileNo, enabled)
  }

  const handleMobileNoChange = (value: string) => {
    setMobileNo(value)
    triggerSearch(username, email, value, enabled)
  }

  const handleEnabledChange = (value: string[] | undefined) => {
    setEnabled(value)
    triggerSearch(username, email, mobileNo, value)
  }

  const handleReset = () => {
    setUsername("")
    setEmail("")
    setMobileNo("")
    setEnabled(undefined)
    if (debounceRef.current) {
      clearTimeout(debounceRef.current)
    }
    loadPage(undefined, undefined, undefined, {})
  }

  const isFiltered = username || email || mobileNo || (enabled && enabled.length > 0)

  return (
    <div className="flex items-center justify-between w-full">
      <div className="flex flex-1 items-center gap-2 flex-wrap">
        <Input
          placeholder="Filter username..."
          value={username}
          onChange={(e) => handleUsernameChange(e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <Input
          placeholder="Filter email..."
          value={email}
          onChange={(e) => handleEmailChange(e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <Input
          placeholder="Filter mobile..."
          value={mobileNo}
          onChange={(e) => handleMobileNoChange(e.target.value)}
          className="h-8 w-[150px] lg:w-[200px]"
        />
        <DataTableFacetedFilter
          column={createFakeColumn("enabled", enabled, handleEnabledChange)}
          title="Enabled"
          options={yesNoOptions}
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
