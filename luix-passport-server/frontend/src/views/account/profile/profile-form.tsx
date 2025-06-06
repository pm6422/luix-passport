import { useState } from "react"
import { useForm } from "react-hook-form"
import {
  Form,
  FormDescription,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form"
import { ProfilePicUploader } from "@/components/custom/uploader/profile-pic-uploader"
import { toast } from "sonner"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { getErrorMessage } from "@/lib/handle-error"
import { AccountService } from "@/services/account-service"
import { LoadingButton } from "@/components/custom/loading-button"

const formSchema = z.object({
  file: z.any(),
  description: z.string().min(1, { message: "Required"})
})

type FormSchema = z.infer<typeof formSchema>

export default function ProfileForm() {
  const [saving, setSaving] = useState(false)
  const form = useForm<FormSchema>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      file: "/api/accounts/profile-pic",
      description: "user avatar"
    },
    mode: "onChange",
  })

  function onSubmit(formData: FormSchema) {
    setSaving(true)
    toast.promise(save(formData), {
      loading: "Updating profile picture...",
      success: () => {
        setSaving(false)
        form.reset()
        return "Updated profile picture"
      },
      error: (error) => {
        setSaving(false)
        return getErrorMessage(error)
      }
    })
  }

  function save(form: FormSchema): Promise<void> {
    const formData = new FormData()
    // parse base64 to file
    formData.append("file", form.file)
    return AccountService.uploadProfilePicture(formData)
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
        <FormField
          control={form.control}
          name="file"
          render={({ field }) => (
            <FormItem  className="flex flex-col items-center">
              <ProfilePicUploader defaultValue={field.value} onValueChange={field.onChange}/>
              <FormDescription>The size of profile picture should be smaller than 15MB.</FormDescription>
              <FormMessage/>
            </FormItem>
          )}
        />
        <div className="flex justify-end">
          <LoadingButton type="submit" loading={saving} disabled={saving || !(form.getValues().file instanceof File)} className="w-full sm:w-auto">
            {saving ? "Saving picture" : "Save picture"}
          </LoadingButton>
        </div>
      </form>
    </Form>
  )
}
