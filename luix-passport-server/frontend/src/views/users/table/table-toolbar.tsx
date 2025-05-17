import { useState } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import InputFormField from "@/components/custom/form-field/input"
import { Button } from "@/components/custom/button"
import { IconPlus, IconFilterSearch } from "@tabler/icons-react"
import { EditDialog } from "../dialog/edit-dialog"
import { yesNo } from "@/data/yes-no"
import { type User, type UserCriteriaSchema, userCriteriaSchema, initialUserCriteriaState } from "@/domains/user"
import { Form } from "@/components/ui/form"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import SelectFormField from '@/components/custom/form-field/select.tsx'

interface DataTableToolbarProps{
  entityName: string,
  loadPage: (pageNo: number | undefined, pageSize: number | undefined, sorts: Array<string> | undefined, criteria: UserCriteriaSchema) => void,
  save: (formData: User) => Promise<void>
}

export function DataTableToolbar ({
  entityName,
  loadPage,
  save
}: DataTableToolbarProps) {
  const [isFilterPopoverOpen, setIsFilterPopoverOpen] = useState(false)
  const form = useForm<UserCriteriaSchema>({
    resolver: zodResolver(userCriteriaSchema),
    defaultValues: initialUserCriteriaState
  })

  function onSubmit(formData: UserCriteriaSchema): void {
    loadPage(undefined, undefined, undefined, formData)
    setIsFilterPopoverOpen(false)
  }

  return (
    <div className="flex items-center justify-between w-full">
      <Popover open={isFilterPopoverOpen} onOpenChange={setIsFilterPopoverOpen}>
        <PopoverTrigger asChild>
          <Button variant="outline" size="icon">
            <IconFilterSearch className="size-4"/>
          </Button>
        </PopoverTrigger>
        <PopoverContent className="max-w-[500px] w-auto p-3" align="start">
          <div className="grid gap-4">
            <Form {...form}>
              <form
                onSubmit={form.handleSubmit(onSubmit)}
                className="flex flex-col gap-4"
              >
                <div className="flex items-center gap-2">
                  <InputFormField 
                    control={form.control} 
                    name="username" 
                    label="Username" 
                    formItemClassName="w-full"
                  />

                  <InputFormField 
                    control={form.control} 
                    name="email" 
                    label="Email" 
                    formItemClassName="w-full"
                  />
                </div>
                <div className="flex items-center gap-2">
                  <InputFormField 
                    control={form.control} 
                    name="mobileNo" 
                    label="Mobile No" 
                    formItemClassName="w-full"
                  />
                  <SelectFormField
                    control={form.control}
                    name="enabled"
                    label="Enabled"
                    options={yesNo}
                    formItemClassName="w-full"
                  />
                </div>
                <div className="flex justify-end gap-2">
                  <Button type="button" variant="outline" className="max-w-20" onClick={() => form.reset()}>
                    Reset
                  </Button>
                  <Button className="max-w-20">
                    Apply
                  </Button>
                </div>
              </form>
            </Form>
          </div>
        </PopoverContent>
      </Popover>
      <div className="space-x-2">
        <EditDialog entityName={entityName} save={save}>
          <Button variant="secondary" size="sm">
            <IconPlus className="mr-2 size-4" aria-hidden="true" />
            Create
          </Button>
        </EditDialog>
      </div>
    </div>
  )
}
