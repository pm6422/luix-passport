import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"
import { Button } from "@/components/ui/button"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { toast } from "sonner"
import { useTheme } from "@/stores/theme-provider"

const formSchema = z.object({
  theme: z.enum(["light", "dark", "system"], {
    required_error: "Please select a theme.",
  }),
})

type FormSchema = z.infer<typeof formSchema>

export function AppearanceForm() {
  const themeProvider = useTheme()

  const form = useForm<FormSchema>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      theme: themeProvider.theme,
    },
  })

  function onSubmit(formData: FormSchema) {
    toast.info("Applied appearance")
    themeProvider.setTheme(formData.theme)
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
        <FormField
          control={form.control}
          name="theme"
          render={({ field }) => (
            <FormItem className="space-y-1">
              <FormLabel>Theme</FormLabel>
              <FormDescription>
                Select the theme for the console.
              </FormDescription>
              <FormMessage />
              <RadioGroup
                onValueChange={field.onChange}
                defaultValue={field.value}
                className="grid max-w-md grid-cols-3 gap-4 pt-2 md:max-w-2xl md:gap-8 lg:max-w-4xl lg:gap-12"
              >
                <FormItem>
                  <FormLabel className="[&:has([data-state=checked])>div>div]:border-primary">
                    <FormControl>
                      <RadioGroupItem value="light" className="sr-only" />
                    </FormControl>
                    <div className="flex flex-col items-center">
                      <div className="items-center rounded-md border-2 border-muted p-1 hover:border-accent">
                        <div className="space-y-2 rounded-sm bg-[#ecedef] p-1 md:p-2">
                          <div className="space-y-2 rounded-md bg-white p-1 shadow-xs md:p-2">
                            <div className="h-2 w-[40px] rounded-lg bg-[#ecedef] md:w-[60px]" />
                            <div className="h-2 w-[60px] rounded-lg bg-[#ecedef] md:w-[100px]" />
                          </div>
                          <div className="flex items-center space-x-2 rounded-md bg-white p-1 shadow-xs md:p-2">
                            <div className="h-4 w-4 rounded-full bg-[#ecedef]" />
                            <div className="h-2 w-[60px] rounded-lg bg-[#ecedef] md:w-[100px]" />
                          </div>
                        </div>
                      </div>
                      <span className="mt-2 block text-center font-normal">
                        Light
                      </span>
                    </div>
                  </FormLabel>
                </FormItem>
                <FormItem>
                  <FormLabel className="[&:has([data-state=checked])>div>div]:border-primary">
                    <FormControl>
                      <RadioGroupItem value="dark" className="sr-only" />
                    </FormControl>
                    <div className="flex flex-col items-center">
                      <div className="items-center rounded-md border-2 border-muted bg-popover p-1 hover:bg-accent hover:text-accent-foreground">
                        <div className="space-y-2 rounded-sm bg-slate-950 p-1 md:p-2">
                          <div className="space-y-2 rounded-md bg-slate-800 p-1 shadow-xs md:p-2">
                            <div className="h-2 w-[40px] rounded-lg bg-slate-400 md:w-[60px]" />
                            <div className="h-2 w-[60px] rounded-lg bg-slate-400 md:w-[100px]" />
                          </div>
                          <div className="flex items-center space-x-2 rounded-md bg-slate-800 p-1 shadow-xs md:p-2">
                            <div className="h-4 w-4 rounded-full bg-slate-400" />
                            <div className="h-2 w-[60px] rounded-lg bg-slate-400 md:w-[100px]" />
                          </div>
                        </div>
                      </div>
                      <span className="mt-2 block text-center font-normal">
                        Dark
                      </span>
                    </div>
                  </FormLabel>
                </FormItem>
                <FormItem>
                  <FormLabel className="[&:has([data-state=checked])>div>div]:border-primary">
                    <FormControl>
                      <RadioGroupItem value="system" className="sr-only" />
                    </FormControl>
                    <div className="flex flex-col items-center">
                      <div className="items-center rounded-md border-2 border-muted bg-popover p-1 hover:bg-accent hover:text-accent-foreground">
                        <div className="space-y-2 rounded-sm bg-[#ecedef] p-1 md:p-2">
                          <div className="space-y-2 rounded-md bg-white p-1 shadow-xs md:p-2">
                            <div className="h-2 w-[40px] rounded-lg bg-[#ecedef] md:w-[60px]" />
                            <div className="h-2 w-[60px] rounded-lg bg-[#ecedef] md:w-[100px]" />
                          </div>
                          <div className="flex items-center space-x-2 rounded-md bg-slate-800 p-1 shadow-xs md:p-2">
                            <div className="h-4 w-4 rounded-full bg-slate-400" />
                            <div className="h-2 w-[60px] rounded-lg bg-slate-400 md:w-[100px]" />
                          </div>
                        </div>
                      </div>
                      <span className="mt-2 block text-center font-normal">
                        System
                      </span>
                    </div>
                  </FormLabel>
                </FormItem>
              </RadioGroup>
            </FormItem>
          )}
        />
        <div className="flex justify-end">
          <Button type="submit" className="w-full sm:w-auto">
            Apply appearance
          </Button>
        </div>
      </form>
    </Form>
  )
}