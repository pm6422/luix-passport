import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from "@/components/ui/carousel";
import { Separator } from "@/components/ui/separator";

export default function LuixRpcPage() {
  // Timeline data
  const timelineItems = [
    {
      date: "2021/12",
      title: "Compatible with JDK 8 through JDK 17",
      description: "Replaced registry and Kryo serialization with extensive code to support JDK 9+."
    },
    {
      date: "2021/08",
      title: "Portal Development Phase",
      description: "Began development of the portal interface."
    },
    {
      date: "2021/06",
      title: "Web Center Development Phase",
      description: "Initiated development of the Web Center module."
    },
    {
      date: "2020/08",
      title: "Key Feature Development Phase",
      description: "Commenced development of core features."
    },
    {
      date: "2020/01",
      title: "Core Module Development Phase",
      description: "Officially started system development."
    },
    {
      date: "2019/10",
      title: "Ideation Stage",
      description: "Decision made to develop a custom RPC framework."
    }
  ];

  return (
    <div className="min-h-screen bg-background">
      {/* Navigation */}
      <nav className="fixed top-0 w-full bg-background z-50 border-b">
        <div className="container flex items-center justify-between h-16">
          <a href="#" className="text-xl font-bold">LUI✘</a>
          <div className="hidden md:flex gap-8">
            {['Home', 'Features', 'Code Examples', 'Design Concepts', 'Development Progress', "Author's Voice", 'Contact'].map((item) => (
              <a key={item} href={`#${item.toLowerCase().replace(/ /g, '-')}`} className="text-sm font-medium hover:text-primary">
                {item}
              </a>
            ))}
          </div>
          <Button variant="ghost" className="md:hidden">
            <MenuIcon className="h-5 w-5" />
          </Button>
        </div>
      </nav>

      {/* Hero Carousel */}
      <section id="home" className="pt-24 pb-12">
        <Carousel className="w-full">
          <CarouselContent>
            <CarouselItem>
              <div className="p-6 text-center">
                <h1 className="text-4xl font-bold tracking-tight mb-4">LUI✘ RPC – A Remote Procedure Call Framework</h1>
                <p className="text-lg text-muted-foreground max-w-3xl mx-auto">
                  A distributed remote call framework that makes remote procedure calls as straightforward as local ones.
                </p>
              </div>
            </CarouselItem>
            <CarouselItem>
              <div className="p-6 text-center">
                <h1 className="text-4xl font-bold tracking-tight mb-4">LUI✘ RPC – Service Governance System</h1>
                <p className="text-lg text-muted-foreground max-w-3xl mx-auto">
                  Offers a comprehensive suite of service governance tools, including application management, server monitoring, and more.
                </p>
              </div>
            </CarouselItem>
          </CarouselContent>
          <CarouselPrevious className="left-4" />
          <CarouselNext className="right-4" />
        </Carousel>
      </section>

      {/* Features Section */}
      <section id="features" className="py-12 bg-muted/50">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Key Features</h2>
            <Separator className="mx-auto w-24 h-1 bg-primary" />
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[
              {
                title: "Interface-Oriented RPC Calls",
                description: "Interface-oriented, TCP-based remote procedure calls that simplify client-side logic."
              },
              {
                title: "Service Registration and Auto-Discovery",
                description: "Applications automatically register their IP addresses with the registry upon startup."
              },
              {
                title: "Fault Tolerance and Load Balancing",
                description: "Clients select optimal addresses for remote calls using advanced load balancing algorithms."
              },
              {
                title: "High-Performance Protocol and Serialization",
                description: "Custom, high-performance TCP protocol with support for multiple serialization algorithms."
              },
              {
                title: "Intelligent Parameter Configuration",
                description: "Built-in algorithms automatically choose optimal parameters to minimize manual setup."
              },
              {
                title: "Service Governance Control Center",
                description: "A powerful, web-based control center for service management and monitoring."
              }
            ].map((feature, index) => (
              <Card key={index}>
                <CardHeader>
                  <CardTitle>{feature.title}</CardTitle>
                </CardHeader>
                <CardContent>
                  <CardDescription>{feature.description}</CardDescription>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Additional Features */}
      <section className="py-12">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Additional Features</h2>
            <Separator className="mx-auto w-24 h-1 bg-primary" />
            <p className="text-muted-foreground mt-4">More exceptional features to enhance your experience</p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="space-y-8">
              {[
                "Multiple Registry Center Types & Instances",
                "Multiple Interface Formats",
                "Retry Count and Request Timeouts",
                "Direct Invocation",
                "Intelligent Serialization & Deserialization",
                "Scheduled Tasks"
              ].map((feature, index) => (
                <div key={index} className="flex items-start gap-4">
                  <div className="bg-primary/10 p-2 rounded-full">
                    <CircleIcon className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <h3 className="font-semibold">{feature}</h3>
                  </div>
                </div>
              ))}
            </div>

            <div className="flex justify-center">
              <div className="space-y-4">
                <img
                  src="/content/img/landing/perspective1.png"
                  alt="Dashboard Screenshot"
                  className="rounded-lg shadow-lg border"
                />
                <img
                  src="/content/img/landing/perspective2.png"
                  alt="Dashboard Screenshot"
                  className="rounded-lg shadow-lg border"
                />
              </div>
            </div>

            <div className="space-y-8">
              {[
                "Concurrency Control & Rate Limiting",
                "Multi-Version Interfaces",
                "Broadcast Calls",
                "Generic Calls",
                "Visual Monitoring & Reporting",
                "Automatic and Manual Service Degradation"
              ].map((feature, index) => (
                <div key={index} className="flex items-start gap-4">
                  <div className="bg-primary/10 p-2 rounded-full">
                    <CircleIcon className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <h3 className="font-semibold">{feature}</h3>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Code Examples */}
      <section id="code-examples" className="py-12 bg-muted/50">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Code Demonstration</h2>
            <Separator className="mx-auto w-24 h-1 bg-primary" />
            <p className="text-muted-foreground mt-4">Examples of server and client setup</p>
          </div>

          <Tabs defaultValue="server" className="w-full">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="server">Server Examples</TabsTrigger>
              <TabsTrigger value="client">Client Examples</TabsTrigger>
            </TabsList>
            <TabsContent value="server">
              <Card>
                <CardHeader>
                  <CardTitle>Server Setup</CardTitle>
                </CardHeader>
                <CardContent className="space-y-6">
                  <div>
                    <h3 className="font-medium mb-2">1. Add LUIX dependency</h3>
                    <div className="bg-muted p-4 rounded-md">
                      <img src="/content/img/code/luix-dependency.png" alt="Dependency" className="w-full max-w-md" />
                    </div>
                  </div>
                  <div>
                    <h3 className="font-medium mb-2">2. Add server interface dependency</h3>
                    <div className="bg-muted p-4 rounded-md">
                      <img src="/content/img/code/common-dependency.png" alt="Dependency" className="w-full max-w-md" />
                    </div>
                  </div>
                  <div>
                    <h3 className="font-medium mb-2">3. Add LUIX configuration</h3>
                    <div className="bg-muted p-4 rounded-md">
                      <img src="/content/img/code/server.png" alt="Configuration" className="w-full max-w-md" />
                    </div>
                  </div>
                  <div>
                    <h3 className="font-medium mb-2">4. Expose server services</h3>
                    <div className="bg-muted p-4 rounded-md">
                      <img src="/content/img/code/provider.png" alt="Service exposure" className="w-full max-w-md" />
                    </div>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
            <TabsContent value="client">
              <Card>
                <CardHeader>
                  <CardTitle>Client Setup</CardTitle>
                </CardHeader>
                <CardContent className="space-y-6">
                  <div>
                    <h3 className="font-medium mb-2">1. Add LUIX dependency</h3>
                    <div className="bg-muted p-4 rounded-md">
                      <img src="/content/img/code/luix-dependency.png" alt="Dependency" className="w-full max-w-md" />
                    </div>
                  </div>
                  <div>
                    <h3 className="font-medium mb-2">2. Add server interface dependency</h3>
                    <div className="bg-muted p-4 rounded-md">
                      <img src="/content/img/code/common-dependency.png" alt="Dependency" className="w-full max-w-md" />
                    </div>
                  </div>
                  <div>
                    <h3 className="font-medium mb-2">3. Add LUIX configuration</h3>
                    <div className="bg-muted p-4 rounded-md">
                      <img src="/content/img/code/client.png" alt="Configuration" className="w-full max-w-md" />
                    </div>
                  </div>
                  <div>
                    <h3 className="font-medium mb-2">4. Reference services on the client</h3>
                    <div className="bg-muted p-4 rounded-md">
                      <img src="/content/img/code/consumer.png" alt="Service reference" className="w-full max-w-md" />
                    </div>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </div>
      </section>

      {/* Design Concepts */}
      <section id="design-concepts" className="py-12">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Product Design Concepts</h2>
            <Separator className="mx-auto w-24 h-1 bg-primary" />
            <p className="text-muted-foreground mt-4">Product Concepts</p>
          </div>

          <div className="grid md:grid-cols-3 gap-8 items-center">
            <div>
              <Badge variant="outline" className="mb-2">LUI✘ RPC</Badge>
              <h3 className="text-2xl font-semibold mb-4">Simplicity</h3>
              <p className="text-muted-foreground">
                The product is designed with simplicity in mind, enabling even recent graduates to get started quickly.
              </p>
            </div>
            <div className="flex justify-center">
              <img
                src="/content/img/landing/screenshot2.png"
                alt="Design concept"
                className="rounded-lg shadow-lg border max-h-80"
              />
            </div>
            <div className="text-right">
              <Badge variant="outline" className="mb-2">LUI✘ RPC</Badge>
              <h3 className="text-2xl font-semibold mb-4">Intelligence</h3>
              <p className="text-muted-foreground">
                With numerous parameters and values, manual selection can be confusing. LUI✘ RPC employs algorithms to automatically select the optimal options.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Development Timeline */}
      <section id="development-progress" className="py-12 bg-muted/50">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Development Progress & Roadmap</h2>
            <Separator className="mx-auto w-24 h-1 bg-primary" />
            <p className="text-muted-foreground mt-4">Progress and Plans</p>
          </div>

          <div className="relative">
            {/* Timeline line */}
            <div className="absolute left-1/2 h-full w-0.5 bg-primary/20 -translate-x-1/2"></div>

            {/* Timeline items */}
            <div className="space-y-8">
              {timelineItems.map((item, index) => (
                <div key={index} className="relative">
                  {/* Dot */}
                  <div className={`absolute top-1/2 h-4 w-4 rounded-full bg-primary -translate-y-1/2 ${index % 2 === 0 ? 'left-1/2 -translate-x-8' : 'right-1/2 translate-x-8'}`}></div>

                  {/* Content */}
                  <div className={`w-full md:w-1/2 p-4 ${index % 2 === 0 ? 'md:pr-8 md:text-right' : 'md:pl-8 md:text-left'}`}
                  >
                    <p className="text-sm text-muted-foreground">{item.date}</p>
                    <h3 className="font-semibold">{item.title}</h3>
                    <p className="text-muted-foreground">{item.description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Author's Voice */}
      <section id="authors-voice" className="py-12 bg-primary text-primary-foreground">
        <div className="container text-center">
          <MessageCircleIcon className="mx-auto h-12 w-12 mb-6" />
          <h2 className="text-3xl font-bold mb-6">Why Develop This Framework?</h2>
          <blockquote className="max-w-3xl mx-auto text-lg italic mb-6">
            "After years of building a knowledge system, I aimed to unify my learnings through a challenging middleware project. Dubbo, an Apache project, is a renowned open-source framework encompassing distributed systems, high performance, and network programming. It represents a massive undertaking that Alibaba invested millions into. Developing a similar system with distinctive features would showcase my capabilities."
          </blockquote>
          <p className="font-medium">2019/10/01 - Louis Lau</p>
        </div>
      </section>

      {/* Thoughts */}
      <section className="py-12">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">What I'm Thinking</h2>
            <Separator className="mx-auto w-24 h-1 bg-primary" />
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {[
              "Remote service calls comprise a significant portion of developers' daily work. Developing HTTP interfaces is costly, and implementing RESTful design can be complex.",
              "Through this project, I'm refining my product design skills, distilling essential RPC features, and crafting a product with a polished, high-quality UI for a comprehensive user experience.",
              "Beyond basic RPC functionality, I'm providing developers with various tools to streamline development, debugging, and troubleshooting."
            ].map((thought, index) => (
              <Card key={index} className="relative">
                <CardContent className="p-6">
                  <div className="absolute top-4 left-4 text-6xl opacity-10">"</div>
                  <p className="pt-8 italic text-muted-foreground">"{thought}"</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Contact */}
      <section id="contact" className="py-12 bg-muted/50">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">Contact Us</h2>
            <Separator className="mx-auto w-24 h-1 bg-primary" />
          </div>

          <div className="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
            <div>
              <h3 className="text-xl font-semibold mb-4">Luix Universe</h3>
              <address className="not-italic space-y-2">
                <p>Amsterdam, Netherlands</p>
                <p>WeChat ID: pm6422</p>
              </address>
            </div>
            <div>
              <h3 className="text-xl font-semibold mb-4">GitHub Resources</h3>
              <div className="space-y-2">
                <p><a href="https://github.com/pm6422" className="text-primary hover:underline">My GitHub Profile</a></p>
                <p><a href="https://github.com/pm6422/passport" className="text-primary hover:underline">Luix Passport</a></p>
                <p><a href="https://github.com/pm6422/luix-rpc" className="text-primary hover:underline">Luix RPC</a></p>
              </div>
            </div>
          </div>

          <div className="text-center mt-12">
            <Button asChild>
              <a href="mailto:louis@luixtech.cn">Send Email</a>
            </Button>

            <div className="flex justify-center gap-4 mt-6">
              <a href="https://github.com/pm6422" target="_blank" rel="noopener noreferrer" className="p-2 rounded-full hover:bg-muted">
                <GithubIcon className="h-6 w-6" />
              </a>
              <a href="http://weibo.cn/luix" target="_blank" rel="noopener noreferrer" className="p-2 rounded-full hover:bg-muted">
                <WeiboIcon className="h-6 w-6" />
              </a>
            </div>

            <p className="mt-8 text-muted-foreground max-w-2xl mx-auto">
              We are seeking talented individuals to join the open-source community. If you're interested, please get in touch.
            </p>
            <p className="mt-4 text-sm text-muted-foreground">&copy; 2021 Luix Universe</p>
          </div>
        </div>
      </section>
    </div>
  );
}

// Icons (import these from your icon library)
function MenuIcon(props: any) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <line x1="4" x2="20" y1="12" y2="12" />
      <line x1="4" x2="20" y1="6" y2="6" />
      <line x1="4" x2="20" y1="18" y2="18" />
    </svg>
  );
}

function CircleIcon(props: any) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <circle cx="12" cy="12" r="10" />
    </svg>
  );
}

function MessageCircleIcon(props: any) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M7.9 20A9 9 0 1 0 4 16.1L2 22Z" />
    </svg>
  );
}

function GithubIcon(props: any) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M15 22v-4a4.8 4.8 0 0 0-1-3.5c3 0 6-2 6-5.5.08-1.25-.27-2.48-1-3.5.28-1.15.28-2.35 0-3.5 0 0-1 0-3 1.5-2.64-.5-5.36-.5-8 0C6 2 5 2 5 2c-.3 1.15-.3 2.35 0 3.5A5.403 5.403 0 0 0 4 9c0 3.5 3 5.5 6 5.5-.39.49-.68 1.05-.85 1.65-.17.6-.22 1.23-.15 1.85v4" />
      <path d="M9 18c-4.51 2-5-2-7-2" />
    </svg>
  );
}

function WeiboIcon(props: any) {
  return (
    <svg
      {...props}
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z" />
    </svg>
  );
}
