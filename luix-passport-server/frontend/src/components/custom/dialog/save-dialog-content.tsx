import { useState, ReactNode } from "react"
import {
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogClose,
  DialogFooter,
  DialogDescription
} from "@/components/ui/dialog"
import { Form } from "@/components/ui/form"
import { UseFormReturn } from "react-hook-form"
import FormErrors from "@/components/custom/form-errors"
import { Button } from "@/components/custom/button"
import { Separator } from "@/components/ui/separator"
import { IconReload } from "@tabler/icons-react"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"
import { cn } from "@/lib/utils"

interface Props {
  children: ReactNode;
  entityName: string;
  id?: string | null;
  form: UseFormReturn<any, any, any>;
  size?: "sm" | "md" | "lg";
  save: (formData: any) => Promise<any>;
  afterSave?: (success: boolean) => void;
  setOpen: (open: boolean) => void;
  readonly?: boolean;
  debug?: boolean;
}

const SaveDialogContent = ({
  children,
  entityName,
  id,
  form,
  size = "md",
  save,
  afterSave,
  setOpen,
  readonly = false,
  debug = false
}: Props) => {
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<Object>({})

  function onSubmit(formData: any): void {
    setSaving(true)
    toast.promise(save(formData), {
      loading: "Saving " + entityName + "...",
      success: () => {
        setOpen(false)
        form.reset()
        afterSave && afterSave(true)
        setSaving(false)
        return "Saved " + entityName
      },
      error: (error) => {
        setOpen(false)
        afterSave && afterSave(false)
        setSaving(false)
        return getErrorMessage(error)
      }
    })
  }

  function onFormError(error: any): void {
    setError(error)
  }

  return (
    <DialogContent className={cn("max-h-screen overflow-y-auto lg:max-w-screen-md", `lg:max-w-screen-${size}`)}>
      <DialogHeader>
        <DialogTitle className="capitalize">{id ? "Update" : "Create"} {entityName}</DialogTitle>
        <DialogDescription></DialogDescription>
      </DialogHeader>
      <Separator/>
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit, onFormError)}
          className="flex flex-col gap-4"
        >
          <FormErrors form={form} error={error}/>
          
          {children}
          
          { debug && 
            <pre className="mt-2 rounded-md bg-slate-950 p-4">
              <code className="text-red-500">{JSON.stringify(form.getValues(), null, 2)}</code>
            </pre>
          }

          <DialogFooter className="gap-2 pt-2 sm:space-x-0">
            <DialogClose asChild>
              <Button type="button" variant="outline" onClick={() => afterSave && afterSave(true)}>
                Cancel
              </Button>
            </DialogClose>
            { !readonly &&
              <Button type="submit" disabled={saving}>
                {saving ? "Saving..." : "Save"}
                {saving && (<IconReload className="ml-1 size-4 animate-spin"/>)}
              </Button>
            }
          </DialogFooter>
        </form>
      </Form>
    </DialogContent>
  )
}

export default SaveDialogContent;
