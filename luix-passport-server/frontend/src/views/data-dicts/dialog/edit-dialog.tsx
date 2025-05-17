import { useState, useEffect, ReactNode } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import ComboboxFormField from "@/components/custom/form-field/combo-box"
import { type Option } from "@/components/custom/form-field/combo-box"
import SwitchFormField from "@/components/custom/form-field/switch"
import { type DataDict, dataDictSchema, initialDataDictState } from "@/domains/data-dict"
import { DataDictService } from "@/services/data-dict-service"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: DataDict) => Promise<void>,
  afterSave?: (success: boolean) => void
}

export function EditDialog({
  children,
  entityName,
  id,
  save,
  afterSave
}: EditDialogProps) {
  const [open, setOpen] = useState(false)
  const [categoryCodeOptions, setCategoryCodeOptions] = useState(Array<Option>)
  const form = useForm<DataDict>({
    resolver: zodResolver(dataDictSchema),
    defaultValues: initialDataDictState
  })

  useEffect(() => {
    if (!open) {
      return
    }
    DataDictService.findAll(true).then(function (res) {
      const options: Array<Option> = Array.from(new Set<string>(res.data.map((item: DataDict) => item.categoryCode))).map(code => ({ label: code, value: code }))
      setCategoryCodeOptions(options)
    })
    id && DataDictService.findById(id).then(r => {
      form.reset(r.data)
    })
  }, [open])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent entityName={entityName} id={id} form={form} save={save} afterSave={afterSave} setOpen={setOpen} debug={true}>
        <ComboboxFormField
          control={form.control} 
          name="categoryCode"
          label="Category Code"
          required
          options={categoryCodeOptions}
          placeholder="Select or input a category code"
          creatable={true}
        />

        <InputFormField 
          control={form.control} 
          name="dictCode" 
          label="Dictionary Code" 
          required
        />

        <InputFormField 
          control={form.control} 
          name="dictName" 
          label="Dictionary Name"
        />

        <InputFormField 
          control={form.control} 
          name="remark" 
          label="Remark"
        />

        <SwitchFormField 
          control={form.control} 
          name="enabled" 
          label="Enabled"
          description="After disabling, existing data can still reference the object, but new data cannot."
        />
      </SaveDialogContent>
    </Dialog>
  )
}
