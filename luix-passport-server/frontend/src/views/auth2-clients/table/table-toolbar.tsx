import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import InputFormField from "@/components/custom/form-field/input"
import { Button } from "@/components/custom/button"
import { IconPlus } from "@tabler/icons-react"
import { EditDialog } from "../dialog/edit-dialog"
import { type Auth2Client, type Auth2ClientCriteriaSchema, auth2ClientCriteriaSchema, initialAuth2ClientCriteriaState } from "@/domains/auth2-client"
import { Form } from "@/components/ui/form"

interface DataTableToolbarProps{
  entityName: string,
  loadPage: (criteria: Auth2ClientCriteriaSchema) => void,
  save: (formData: Auth2Client) => Promise<void>
}

export function DataTableToolbar ({
  entityName,
  loadPage,
  save
}: DataTableToolbarProps) {
  const form = useForm<Auth2ClientCriteriaSchema>({
    resolver: zodResolver(auth2ClientCriteriaSchema),
    defaultValues: initialAuth2ClientCriteriaState
  })

  function onSubmit(formData: Auth2ClientCriteriaSchema): void {
    loadPage(formData)
  }

  return (
    <div className="flex items-center justify-between w-full">
      <div className="grid gap-4">
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(onSubmit)}
            className="flex flex-col gap-4"
          >
              <InputFormField 
                control={form.control} 
                name="keyword" 
                placeholder="Filter by keyword"
                onKeyDown={e => e.key === 'Enter' ? form.handleSubmit(onSubmit) : ''}
              />
          </form>
        </Form>
      </div>
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
