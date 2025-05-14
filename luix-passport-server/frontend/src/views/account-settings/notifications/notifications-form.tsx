import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"

import { Button } from "@/components/custom/button"
import RadioGroupFormField from "@/components/custom/form-field/radio-group"
import { Checkbox } from "@/components/ui/checkbox"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
} from "@/components/ui/form"
import { Switch } from "@/components/ui/switch"
import { toast } from "sonner"
import { Link } from "react-router-dom"

const notificationsFormSchema = z.object({
  type: z.enum(["all", "mentions", "none"], {
    required_error: "You need to select a notification type.",
  }),
  mobile: z.boolean().default(false).optional(),
  communication_emails: z.boolean().default(false).optional(),
  social_emails: z.boolean().default(false).optional(),
  marketing_emails: z.boolean().default(false).optional(),
  security_emails: z.boolean(),
})

type NotificationsFormValues = z.infer<typeof notificationsFormSchema>

// This can come from your database or API.
const defaultValues: Partial<NotificationsFormValues> = {
  communication_emails: false,
  marketing_emails: false,
  social_emails: true,
  security_emails: true,
}

export function NotificationsForm() {
  const form = useForm<NotificationsFormValues>({
    resolver: zodResolver(notificationsFormSchema),
    defaultValues,
  })

  function onSubmit(data: NotificationsFormValues) {
    toast(
      <div>
        <span>You submitted the following values:</span>
        <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
          <code className="text-white">{JSON.stringify(data, null, 2)}</code>
        </pre>
      </div>, 
    { duration: 5000 })
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
        <RadioGroupFormField
          control={form.control}
          name="type"
          label="Notify me about..."
          options={[
            { value: "all", label: "All new messages" },
            { value: "mentions", label: "Direct messages and mentions" },
            { value: "none", label: "Nothing" }
          ]}
        />

        <div>
          <h3 className="mb-4 text-lg font-medium">Email Notifications</h3>
          <div className="space-y-4">
            <FormField
              control={form.control}
              name="communication_emails"
              render={({ field }) => (
                <FormItem className="flex flex-row items-center justify-between rounded-lg border p-4">
                  <div className="space-y-0.5">
                    <FormLabel className="text-base">
                      Communication emails
                    </FormLabel>
                    <FormDescription>
                      Receive emails about your account activity.
                    </FormDescription>
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="marketing_emails"
              render={({ field }) => (
                <FormItem className="flex flex-row items-center justify-between rounded-lg border p-4">
                  <div className="space-y-0.5">
                    <FormLabel className="text-base">
                      Marketing emails
                    </FormLabel>
                    <FormDescription>
                      Receive emails about new products, features, and more.
                    </FormDescription>
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="social_emails"
              render={({ field }) => (
                <FormItem className="flex flex-row items-center justify-between rounded-lg border p-4">
                  <div className="space-y-0.5">
                    <FormLabel className="text-base">Social emails</FormLabel>
                    <FormDescription>
                      Receive emails for friend requests, follows, and more.
                    </FormDescription>
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="security_emails"
              render={({ field }) => (
                <FormItem className="flex flex-row items-center justify-between rounded-lg border p-4">
                  <div className="space-y-0.5">
                    <FormLabel className="text-base">Security emails</FormLabel>
                    <FormDescription>
                      Receive emails about your account activity and security.
                    </FormDescription>
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                      disabled
                      aria-readonly
                    />
                  </FormControl>
                </FormItem>
              )}
            />
          </div>
        </div>
        <FormField
          control={form.control}
          name="mobile"
          render={({ field }) => (
            <FormItem className="flex flex-row items-start space-x-3 space-y-0">
              <FormControl>
                <Checkbox
                  checked={field.value}
                  onCheckedChange={field.onChange}
                />
              </FormControl>
              <div className="space-y-1 leading-none">
                <FormLabel>
                  Use different settings for my mobile devices
                </FormLabel>
                <FormDescription>
                  You can manage your mobile notifications in the{" "}
                  <Link
                    to="/account-settings"
                    className="underline decoration-dashed underline-offset-4 hover:decoration-solid"
                  >
                    mobile settings
                  </Link>{" "}
                  page.
                </FormDescription>
              </div>
            </FormItem>
          )}
        />
        <Button type="submit">Update notifications</Button>
      </form>
    </Form>
  )
}
