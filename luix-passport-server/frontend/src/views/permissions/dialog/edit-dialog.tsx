import { useState, useEffect, ReactNode } from 'react'
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { Dialog, DialogTrigger } from "@/components/ui/dialog"
import SaveDialogContent from "@/components/custom/dialog/save-dialog-content"
import InputFormField from "@/components/custom/form-field/input"
import SelectFormField from "@/components/custom/form-field/select"
import { type Permission, permissionSchema, initialPermissionState } from "@/domains/permission"
import { PermissionService } from "@/services/permission-service"
import { DataDictService } from "@/services/data-dict-service"
import { Option } from "@/components/custom/multi-select"
import type { DataDict } from "@/domains/data-dict"

interface EditDialogProps {
  children: ReactNode,
  entityName: string,
  id?: string | null,
  save: (formData: Permission) => Promise<void>,
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
  const [resourceTypes, setResourceTypes] = useState(Array<Option>)
  const [actions, setActions] = useState(Array<Option>)
  const form = useForm<Permission>({
    resolver: zodResolver(permissionSchema),
    defaultValues: initialPermissionState
  })

  useEffect(() => {
    if (!open) {
      return
    }
    Promise.all([
      DataDictService.lookup("PermissionResourceType", true),
      DataDictService.lookup("PermissionAction", true),
    ]).then(results => {
      // load options
      setResourceTypes(results[0].data.map((item: DataDict) => ({label: item.dictCode , value: item.dictCode})));
      setActions(results[1].data.map((item: DataDict) => ({label: item.dictCode , value: item.dictCode})));

      if (id) {
        PermissionService.findById(id).then(r => {
          form.reset(r.data)
        })
      }
    })
  }, [form, id, open])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <SaveDialogContent<Permission> entityName={entityName} id={id} form={form} save={save} afterSave={afterSave} setOpen={setOpen}>

        <InputFormField
          control={form.control}
          name="id"
          label="ID"
          required
        />

        <SelectFormField
          control={form.control}
          name="resourceType"
          label="Resource Type"
          options={resourceTypes}
          formItemClassName="w-full"
          required
        />

        <SelectFormField
          control={form.control}
          name="action"
          label="Action"
          options={actions}
          formItemClassName="w-full"
          required
        />

        <InputFormField
          control={form.control}
          name="remark"
          label="Remark"
        />

      </SaveDialogContent>
    </Dialog>
  )
}