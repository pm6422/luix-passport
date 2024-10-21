import { Link } from "react-router-dom"
import { nord } from "react-syntax-highlighter/dist/esm/styles/prism"
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter"
import { LayoutBody } from "@/layouts/layout-definitions"
import { Breadcrumb, BreadcrumbItem } from "@/components/custom/breadcrumb"
import { PinInput, PinInputField } from "@/components/custom/pin-input"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Separator } from "@/components/ui/separator"
import { Input } from "@/components/ui/input"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormDescription
} from "@/components/ui/form"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { toast } from "sonner"
import { cn } from "@/libs/utils"
import { IconCalendar } from "@tabler/icons-react"
import { Button } from "@/components/custom/button"
import { Calendar } from "@/components/ui/calendar"
import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { formatDate } from "@/libs/utils"

export default function FormExample() {
  const { authUser } = useStore(authUserStore)
  const items = [
    { title: "Extra Components", href: "/extra-components" },
    { title: "Breadcrumb" },
  ].map(({ href, title }) => (
    <BreadcrumbItem key={title}>
      {href ? (
        <Link
          className="text-muted-foreground underline decoration-muted-foreground decoration-dashed underline-offset-4 hover:text-foreground hover:decoration-solid"
          to={href}
        >
          {title}
        </Link>
      ) : (
        <span className="text-muted-foreground">{title}</span>
      )}
    </BreadcrumbItem>
  ))

  const FormSchema = z.object({
    language: z.string().optional(),
    dob: z.date({ required_error: "A date of birth is required." }),
  });

  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: {
      language: "en",
      dob: new Date("2023-01-23")
    },
  });

  function onSubmit(data: z.infer<typeof FormSchema>) {
    toast(
      `You have selected following frameworks: ${data}.`
    );
  }

  return (
    <LayoutBody className="space-y-4">
      <div className="flex items-center justify-between space-y-2">
        <h1 className="text-2xl font-bold tracking-tight md:text-3xl">
          Extra Components
        </h1>
      </div>
      <h2 className="text-lg font-bold md:text-xl">Breadcrumbs</h2>
      <Breadcrumb>{items}</Breadcrumb>

      <Separator />

      <h2 className="text-lg font-bold md:text-xl">Pin Input</h2>
      <div className="flex flex-col gap-12 lg:flex-row">
        <div className="flex-1">
          <h3 className="mb-2 font-medium">Uncontrolled</h3>
          <Tabs defaultValue="preview">
            <TabsList>
              <TabsTrigger value="preview">Preview</TabsTrigger>
              <TabsTrigger value="code">Code</TabsTrigger>
            </TabsList>
            <TabsContent value="preview">
              <div className="flex min-h-56 items-center justify-center rounded border">
                <PinInput
                  className="flex h-10 space-x-4"
                  onComplete={(str) => console.log("completed", str)}
                  autoFocus
                >
                  <PinInputField component={Input} />
                  <PinInputField component={Input} />
                  <Separator orientation="vertical" />
                  <PinInputField component={Input} />
                  <PinInputField component={Input} />
                </PinInput>
              </div>
            </TabsContent>
            <TabsContent value="code">
              <SyntaxHighlighter
                language="tsx"
                style={nord}
                wrapLines
                wrapLongLines
              >
                {`<PinInput
                  className="flex h-10 space-x-4"
                  defaultValue=""
                  onComplete={(str) => 
                    console.log("completed", str)
                  }  
                  autoFocus
                >
                  <PinInputField component={Input} />
                  <PinInputField component={Input} />
                  <Separator orientation="vertical" />
                  <PinInputField component={Input} />
                  <PinInputField component={Input} />
                </PinInput>
                `}
              </SyntaxHighlighter>
            </TabsContent>
          </Tabs>
        </div>
      </div>

      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSubmit)}
          className="flex flex-col gap-4"
        >

          <FormField
            control={form.control}
            name="dob"
            render={({ field }) => (
              <FormItem className="flex flex-col">
                <FormLabel>Date of birth</FormLabel>
                <Popover>
                  <PopoverTrigger asChild>
                    <FormControl>
                      <Button
                        variant={"outline"}
                        className={cn(
                          "w-[240px] pl-3 text-left font-normal",
                          !field.value && "text-muted-foreground"
                        )}
                      >
                        {field.value ? (
                          formatDate(authUser.dateTimeFormatInstance.dateFormat, field.value, authUser.timeZone)
                        ) : (
                          <span>Pick a date</span>
                        )}
                        <IconCalendar className="ml-auto h-4 w-4 opacity-50" />
                      </Button>
                    </FormControl>
                  </PopoverTrigger>
                  <PopoverContent className="w-auto p-0" align="start">
                    <Calendar
                      mode="single"
                      selected={field.value}
                      onSelect={field.onChange}
                      disabled={(date: Date) =>
                        date > new Date() || date < new Date("1900-01-01")
                      }
                      initialFocus
                    />
                  </PopoverContent>
                </Popover>
                <FormDescription>
                  Your date of birth is used to calculate your age.
                </FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />

        </form>
      </Form>

      
    
    </LayoutBody>
  )
}
