import { useState, useEffect, ReactNode, useRef } from "react"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm, FieldValues, UseFormReturn, DefaultValues, FieldErrors } from "react-hook-form"
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogClose,
  DialogFooter,
  DialogDescription
} from "@/components/ui/dialog"
import { Form } from "@/components/ui/form"
import FormErrors from "@/components/custom/form-element/form-errors"
import { Button } from "@/components/ui/button"
import { LoadingButton } from "@/components/custom/form-element/loading-button"
import { Separator } from "@/components/ui/separator"
import { toast } from "sonner"
import { getErrorMessage } from "@/lib/handle-error"
import { cn } from "@/lib/utils"
import type { z } from "zod"

interface LoadResult<TFV extends FieldValues, TOptions = unknown> {
  options?: TOptions
  entity?: TFV
}

// Type helper removed to avoid generic identity issues

interface FormDialogProps<
  TFieldValues extends FieldValues,
  TOptions = unknown
> {
  children: ReactNode
  entityName: string
  id?: string | null
  schema: z.ZodType<TFieldValues, z.ZodTypeDef, unknown>
  defaultValues: DefaultValues<TFieldValues>
  save?: (formData: TFieldValues) => Promise<void>
  afterSave?: (success: boolean) => void
  onLoad?: (id?: string | null) => Promise<LoadResult<TFieldValues, TOptions>>
  renderFields: (form: UseFormReturn<TFieldValues, unknown, TFieldValues>, options: TOptions) => ReactNode
  size?: "sm" | "md" | "lg"
  readonly?: boolean
  debug?: boolean
}

interface Props<T extends FieldValues> {
  children: ReactNode
  entityName: string
  id?: string | null
  form: UseFormReturn<T, unknown, T>
  size?: "sm" | "md" | "lg"
  save?: (formData: T) => Promise<void>
  afterSave?: (success: boolean) => void
  setOpen: (open: boolean) => void
  readonly?: boolean
  debug?: boolean
}

function FormDialogContent<T extends FieldValues>({
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
}: Props<T>) {
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<FieldErrors>({})

  function onSubmit(formData: T): void {
    setSaving(true)
    if (save) {
      toast.promise(save(formData), {
        loading: "Saving " + entityName + "...",
        success: () => {
          setOpen(false)
          form.reset()
          if (afterSave) {
            afterSave(true)
          }
          setSaving(false)
          return "Saved " + entityName
        },
        error: (error) => {
          setOpen(false)
          if (afterSave) {
            afterSave(false)
          }
          setSaving(false)
          return getErrorMessage(error)
        }
      })
    }
  }

  function onFormError(error: FieldErrors): void {
    setError(error)
  }

  return (
    <DialogContent className={cn("max-h-screen overflow-y-auto lg:max-w-screen-md", `lg:max-w-screen-${size}`)}>
      <DialogHeader>
        <DialogTitle className="capitalize">{readonly ? "View" : id ? "Update" : "Create"} {entityName}</DialogTitle>
        <DialogDescription></DialogDescription>
      </DialogHeader>
      <Separator />
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit, onFormError)}
          className="flex flex-col gap-4"
        >
          <FormErrors form={form} error={error} />

          {children}

          {debug &&
            <pre className="mt-2 rounded-md bg-slate-950 p-4">
              <code className="text-red-500">{JSON.stringify(form.getValues(), null, 2)}</code>
            </pre>
          }

          <DialogFooter className="gap-2 pt-2 sm:space-x-0">
            <DialogClose asChild>
              <Button type="button" variant="outline">
                Cancel
              </Button>
            </DialogClose>
            {!readonly &&
              <LoadingButton type="submit" loading={saving}>
                {saving ? "Saving" : "Save"}
              </LoadingButton>
            }
          </DialogFooter>
        </form>
      </Form>
    </DialogContent>
  )
}

export function FormDialog<
  TFieldValues extends FieldValues,
  TOptions = unknown
>({
  children,
  entityName,
  id,
  schema,
  defaultValues,
  save,
  afterSave,
  onLoad,
  renderFields,
  size = "md",
  readonly = false,
  debug = false,
}: FormDialogProps<TFieldValues, TOptions>) {
  const [open, setOpen] = useState(false)
  const [options, setOptions] = useState<TOptions>({} as TOptions)
  const [loading, setLoading] = useState(false)
  const onLoadRef = useRef(onLoad)

  useEffect(() => {
    onLoadRef.current = onLoad
  }, [onLoad])

  const form = useForm<TFieldValues, unknown, TFieldValues>({
    // @ts-expect-error - zodResolver has complex type constraints that are difficult to satisfy with generic schemas
    resolver: zodResolver(schema),
    defaultValues,
  })

  useEffect(() => {
    if (!open) {
      return
    }

    const loadData = async () => {
      setLoading(true)
      try {
        const loader = onLoadRef.current
        if (loader) {
          const result = await loader(id)

          if (result.options) {
            setOptions(result.options)
          }

          if (result.entity) {
            form.reset(result.entity)
          }
        }
      } catch (error) {
        console.error("Failed to load data:", error)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [open, id, form])

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {children}
      </DialogTrigger>
      <FormDialogContent<TFieldValues>
        entityName={entityName}
        id={id}
        form={form}
        save={save}
        afterSave={afterSave}
        setOpen={setOpen}
        size={size}
        readonly={readonly}
        debug={debug}
      >
        {loading ? (
          <div className="flex items-center justify-center py-8">
            <div className="text-sm text-muted-foreground">Loading...</div>
          </div>
        ) : (
          renderFields(form, options)
        )}
      </FormDialogContent>
    </Dialog>
  )
}
