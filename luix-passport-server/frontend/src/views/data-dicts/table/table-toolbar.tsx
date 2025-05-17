import { useState } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Button } from "@/components/custom/button"
import InputFormField from "@/components/custom/form-field/input"
import { IconPlus, IconUpload, IconFilterSearch } from "@tabler/icons-react"
import { EditDialog } from "../dialog/edit-dialog"
import { type DataDict, type DataDictCriteria, dataDictCriteriaSchema, initialDataDictCriteriaState } from "@/domains/data-dict"
import { type UploadFormSchema } from "@/components/custom/uploader/upload-dialog"
import { UploadDialog } from "@/components/custom/uploader/upload-dialog"
import { Form } from "@/components/ui/form"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { yesNoOptions } from '@/data/yes-no-options'
import SelectFormField from '@/components/custom/form-field/select'

interface DataTableToolbarProps{
  entityName: string,
  loadPage: (pageNo: number | undefined, pageSize: number | undefined, sorts: Array<string> | undefined, criteria: DataDictCriteria) => void,
  save: (formData: DataDict) => Promise<void>,
  upload: (formData: UploadFormSchema) => Promise<Array<void>>
}

export function DataTableToolbar ({
  entityName,
  loadPage,
  save,
  upload
}: DataTableToolbarProps) {
  const [isFilterPopoverOpen, setIsFilterPopoverOpen] = useState(false)
  const form = useForm<DataDictCriteria>({
    resolver: zodResolver(dataDictCriteriaSchema),
    defaultValues: initialDataDictCriteriaState
  })

  function onSubmit(formData: DataDictCriteria): void {
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
                <div className="flex flex-col sm:flex-row gap-2">
                  <InputFormField 
                    control={form.control} 
                    name="num" 
                    label="Number" 
                    formItemClassName="w-full"
                  />

                  <InputFormField 
                    control={form.control} 
                    name="categoryCode" 
                    label="Category Code" 
                    formItemClassName="w-full"
                  />

                  <SelectFormField
                    control={form.control}
                    name="enabled"
                    label="Enabled"
                    options={yesNoOptions}
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
        <UploadDialog entityName={entityName} upload={upload} description="Supported file types: .txt, .json, you can use downloaded the template." templateUrl="api/data-dicts/import-template">
          <Button variant="secondary" size="sm">
            <IconUpload className="mr-2 size-4" aria-hidden="true" />
            Upload
          </Button>
        </UploadDialog>
      </div>
    </div>
  )
}
