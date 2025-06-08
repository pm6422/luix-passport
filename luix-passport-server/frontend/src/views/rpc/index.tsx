import { useState, useRef } from "react"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Carousel, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from "@/components/ui/carousel";
import Autoplay from "embla-carousel-autoplay"
import { Separator } from "@/components/ui/separator";
import {
  IconBrandGithub,
  IconBrandWeibo,
  IconMessageCircle,
  IconCircle,
  IconMenu2,
  IconExternalLink,
  IconMail,
  IconCalendarCheck,
  IconDatabase,
  IconCode,
  IconReload,
  IconRocket,
  IconBinary,
  IconClock,
  IconSpeedboat,
  IconVersions,
  IconBroadcast,
  IconApi,
  IconChartBar,
  IconShieldHalf,
  IconServer,
  IconDeviceLaptop
} from "@tabler/icons-react";

// ====================== COMPONENT ======================
export default function LuixRpcPage() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const plugin = useRef(
    Autoplay({ delay: 4000, stopOnInteraction: true })
  )

  // ====================== TEXT CONTENT ======================
  const content = {
    navigation: {
      brand: "LUI✘ RPC",
      links: ['Home', 'Features', 'Code Examples', 'Design Concepts', 'Development Progress', "Author's Voice", 'Contact']
    },
    hero: [
      {
        title: "LUI✘ RPC – A Remote Procedure Call Framework",
        description: "A distributed remote call framework that makes remote procedure calls as straightforward as local ones."
      },
      {
        title: "LUI✘ RPC – Service Governance System",
        description: "Offers a comprehensive suite of service governance tools, including application management, server monitoring, and more."
      }
    ],
    features: {
      title: "Key Features",
      subtitle: "Comprehensive solution for distributed system communication",
      items: [
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
      ]
    },
    additionalFeatures: {
      title: "Additional Features",
      subtitle: "More exceptional features to enhance your experience",
      leftColumn: [
        {
          title: "Multiple Registry Center Types & Instances",
          description: "Supports Consul and ZooKeeper registries and reserves extension interfaces for future integrations. An application can also register with multiple registries of different types simultaneously.",
          icon: IconDatabase
        },
        {
          title: "Multiple Interface Formats",
          description: "A single service interface can have multiple implementation classes to meet different needs, all coexisting permanently. Example: separate implementations for mobile and PC versions.",
          icon: IconCode
        },
        {
          title: "Retry Count and Request Timeouts",
          description: "Sets retry count for repeated requests in case of RPC call failures, along with a request timeout. If a response isn’t received within the timeout period, the call is treated as a failure.",
          icon: IconReload
        },
        {
          title: "Direct Invocation",
          description: "The client can discover and call services through the registry, or skip the registry and call the server directly, similar to an HTTP call using the configured server address.",
          icon: IconRocket
        },
        {
          title: "Intelligent Serialization & Deserialization",
          description: "The system automatically chooses the most suitable serialization algorithm based on data type, bypassing incompatible algorithms like Hessian if Serializable is not implemented.",
          icon: IconBinary
        },
        {
          title: "Scheduled Tasks",
          description: "The LUIX RPC Web Center includes a scheduling system that uses CRON expressions or fixed intervals to initiate RPC calls, with support for timeout settings, retries, fault tolerance, and reports..",
          icon: IconClock
        }
      ],
      rightColumn: [
        {
          title: "Concurrency Control & Rate Limiting",
          description: "The system offers traffic control capabilities, including algorithms to limit client and server concurrent threads, along with multiple request limit algorithms, such as QPS rate limiting..",
          icon: IconSpeedboat
        },
        {
          title: "Multi-Version Interfaces",
          description: "A single service interface can have different implementation versions, a temporary coexistence measure for seamless user experience during production upgrades.",
          icon: IconVersions
        },
        {
          title: "Broadcast Calls",
          description: "Clients can issue a single request that simultaneously calls all server programs under an interface, ideal for scenarios like synchronizing cache values across nodes.",
          icon: IconBroadcast
        },
        {
          title: "Generic Calls",
          description: "Clients can complete RPC calls without server interface dependencies, useful for cases like PHP clients calling Java interfaces without importing Java dependencies.",
          icon: IconApi
        },
        {
          title: "Visual Monitoring & Reporting",
          description: "The LUIX RPC Web Center lists all applications, services, and server hardware configurations and offers graphs for service dependency, scheduled tasks, and various reports.",
          icon: IconChartBar
        },
        {
          title: "Automatic and Manual Service Degradation",
          description: "The LUI️X RPC Web Center provides multiple automatic service downgrade configurations, including scheduled and conditional downgrades, and also supports manual downgrade.",
          icon: IconShieldHalf
        }
      ]
    },
    codeExamples: {
      title: "Code Demonstration",
      subtitle: "Examples of server and client setup",
      server: {
        title: "Server Setup",
        steps: [
          { title: "1. Add LUIX dependency", image: "/assets/images/rpc/code/luix-dependency.png" },
          { title: "2. Add server interface dependency", image: "/assets/images/rpc/code/common-dependency.png" },
          { title: "3. Add LUIX configuration", image: "/assets/images/rpc/code/server.png" },
          { title: "4. Expose server services", image: "/assets/images/rpc/code/provider.png" }
        ]
      },
      client: {
        title: "Client Setup",
        steps: [
          { title: "1. Add LUIX dependency", image: "/assets/images/rpc/code/luix-dependency.png" },
          { title: "2. Add server interface dependency", image: "/assets/images/rpc/code/common-dependency.png" },
          { title: "3. Add LUIX configuration", image: "/assets/images/rpc/code/client.png" },
          { title: "4. Reference services on the client", image: "/assets/images/rpc/code/consumer.png" }
        ]
      }
    },
    designConcepts: {
      title: "Product Design Concepts",
      subtitle: "Product Concepts",
      simplicity: {
        badge: "LUI✘ RPC",
        title: "Simplicity",
        description: "The product is designed with simplicity in mind, enabling even recent graduates to get started quickly."
      },
      intelligence: {
        badge: "LUI✘ RPC",
        title: "Intelligence",
        description: "With numerous parameters and values, manual selection can be confusing. LUI✘ RPC employs algorithms to automatically select the optimal options."
      }
    },
    timeline: {
      title: "Development Progress & Roadmap",
      subtitle: "Progress and Plans",
      items: [
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
      ]
    },
    authorVoice: {
      title: "Why Develop This Framework?",
      quote: "After years of building a knowledge system, I aimed to unify my learnings through a challenging middleware project. Dubbo, an Apache project, is a renowned open-source framework encompassing distributed systems, high performance, and network programming. It represents a massive undertaking that Alibaba invested millions into. Developing a similar system with distinctive features would showcase my capabilities.",
      signature: "2019/10/01 - Louis Lau"
    },
    thoughts: {
      title: "What I'm Thinking",
      items: [
        "Remote service calls comprise a significant portion of developers' daily work. Developing HTTP interfaces is costly, and implementing RESTful design can be complex.",
        "Through this project, I'm refining my product design skills, distilling essential RPC features, and crafting a product with a polished, high-quality UI for a comprehensive user experience.",
        "Beyond basic RPC functionality, I'm providing developers with various tools to streamline development, debugging, and troubleshooting."
      ]
    },
    contact: {
      title: "Contact Us",
      info: {
        title: "Contact Information",
        items: [
          "Amsterdam, Netherlands",
          "WeChat ID: pm6422"
        ]
      },
      resources: {
        title: "GitHub Resources",
        items: [
          { text: "My GitHub Profile", url: "https://github.com/pm6422" },
          { text: "Luix Passport", url: "https://github.com/pm6422/passport" },
          { text: "Luix RPC", url: "https://github.com/pm6422/luix-rpc" }
        ]
      },
      footer: {
        text: "We are seeking talented individuals to join the open-source community. If you're interested, please get in touch.",
        copyright: "© 2025 Luix Universe"
      }
    }
  };

  return (
    <div className="min-h-screen">
      {/* Navigation */}
      <nav className="fixed top-0 w-full bg-background z-50 border-b">
        <div className="container flex items-center justify-between h-16 relative">
          <a href="#" className="flex items-center gap-2 text-xl font-bold">
            <img src="/assets/images/rpc/logo.png" alt="Logo" className="size-10" />
            {content.navigation.brand}
          </a>

          {/* 大屏链接 */}
          <div className="hidden md:flex gap-8">
            {content.navigation.links.map((item) => (
              <a
                key={item}
                href={`#${item.toLowerCase().replace(/ /g, '-')}`}
                className="text-sm font-medium hover:text-primary"
              >
                {item}
              </a>
            ))}
          </div>

          {/* 移动端菜单按钮 */}
          <Button
            variant="ghost"
            className="md:hidden"
            onClick={() => setMobileMenuOpen(prev => !prev)}
          >
            <IconMenu2 className="h-5 w-5" />
          </Button>

          {/* 移动端浮层菜单（在小屏幕时显示） */}
          {mobileMenuOpen && (
            <div className="absolute top-full left-0 w-full bg-background border-t border-muted z-40 md:hidden">
              <div className="flex flex-col py-4 space-y-2">
                {content.navigation.links.map((item) => (
                  <a
                    key={item}
                    href={`#${item.toLowerCase().replace(/ /g, '-')}`}
                    className="px-4 py-2 text-base font-medium hover:bg-muted"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    {item}
                  </a>
                ))}
              </div>
            </div>
          )}
        </div>
      </nav>

      {/* Hero Carousel */}
      <section id="home" className="pt-24 pb-12">
        <div className="relative h-[300px] overflow-hidden rounded-lg">
          {/* Animated SVG Background */}
          <div className="absolute inset-0 -z-10 overflow-hidden">
            <svg
              className="absolute w-full h-full"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 1440 320"
              preserveAspectRatio="none"
            >
              <defs>
                <linearGradient id="waveGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stopColor="#FF007F" /> {/* 鲜艳玫红 */}
                  <stop offset="33%" stopColor="#00F0FF" /> {/* 亮青色 */}
                  <stop offset="66%" stopColor="#FFEE00" /> {/* 纯黄色 */}
                  <stop offset="100%" stopColor="#FF5100" /> {/* 橙红色 */}
                </linearGradient>
              </defs>
              <path
                fill="url(#waveGradient)"
                d="M0,192L48,197.3C96,203,192,213,288,229.3C384,245,480,267,576,250.7C672,235,768,181,864,181.3C960,181,1056,235,1152,234.7C1248,235,1344,181,1392,154.7L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"
              >
                <animate
                  attributeName="d"
                  values="
                  M0,192L48,197.3C96,203,192,213,288,229.3C384,245,480,267,576,250.7C672,235,768,181,864,181.3C960,181,1056,235,1152,234.7C1248,235,1344,181,1392,154.7L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z;
                  M0,128L48,154.7C96,181,192,235,288,234.7C384,235,480,181,576,181.3C672,181,768,235,864,250.7C960,267,1056,245,1152,229.3C1248,213,1344,203,1392,197.3L1440,192L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z;
                  M0,160L48,170.7C96,181,192,203,288,213.3C384,224,480,224,576,208C672,192,768,160,864,165.3C960,171,1056,213,1152,208C1248,203,1344,149,1392,122.7L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z;
                  M0,192L48,197.3C96,203,192,213,288,229.3C384,245,480,267,576,250.7C672,235,768,181,864,181.3C960,181,1056,235,1152,234.7C1248,235,1344,181,1392,154.7L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z
                "
                  dur="20s"
                  repeatCount="indefinite"
                />
                <animate
                  attributeName="fill-opacity"
                  values="0.7;0.9;0.8;0.9"
                  dur="15s"
                  repeatCount="indefinite"
                />
              </path>
            </svg>
          </div>
          <Carousel className="w-full h-full" plugins={[plugin.current]}>
            <CarouselContent className="h-full">
              {content.hero.map((item, index) => (
                <CarouselItem key={index} className="h-full flex items-center justify-center">
                  <div className="p-6 text-center max-w-3xl mx-auto">
                    <h1 className="text-4xl md:text-5xl font-bold tracking-tight mb-6">{item.title}</h1>
                    <p className="text-lg md:text-xl text-muted-foreground">
                      {item.description}
                    </p>
                  </div>
                </CarouselItem>
              ))}
            </CarouselContent>
            <CarouselPrevious className="carousel-prev-button left-6 h-12 w-12 scale-150" />
            <CarouselNext className="carousel-next-button right-6 h-12 w-12 scale-150" />
          </Carousel>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-12 bg-muted/50">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">{content.features.title}</h2>
            <Separator className="mx-auto w-24 h-1" />
            <p className="text-muted-foreground mt-4">{content.features.subtitle}</p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {content.features.items.map((feature, index) => (
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
            <h2 className="text-3xl font-bold mb-4">{content.additionalFeatures.title}</h2>
            <Separator className="mx-auto w-24 h-1" />
            <p className="text-muted-foreground mt-4">{content.additionalFeatures.subtitle}</p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {/* Left Column */}
            <div className="space-y-8">
              {content.additionalFeatures.leftColumn.map((item, index) => (
                <div key={index} className="flex items-start gap-4 md:py-6">
                  <div className="bg-primary/10 p-2 rounded-full shrink-0">
                    <item.icon className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <h3 className="font-semibold mb-2">{item.title}</h3>
                    <p className="text-sm text-muted-foreground">{item.description}</p>
                  </div>
                </div>
              ))}
            </div>

            {/* Center Images */}
            <div className="flex justify-center hidden md:flex">
              <div className="space-y-4 sticky top-20 h-fit">
                <img
                  src="/assets/images/rpc/perspective1.png"
                  alt="Dashboard Screenshot"
                />
                <img
                  src="/assets/images/rpc/perspective2.png"
                  alt="Dashboard Screenshot"
                />
              </div>
            </div>

            {/* Right Column */}
            <div className="space-y-8">
              {content.additionalFeatures.rightColumn.map((item, index) => (
                <div key={index} className="flex items-start gap-4 md:py-6">
                  <div className="bg-primary/10 p-2 rounded-full shrink-0">
                    <item.icon className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <h3 className="font-semibold mb-2">{item.title}</h3>
                    <p className="text-sm text-muted-foreground">{item.description}</p>
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
            <h2 className="text-3xl font-bold mb-4">{content.codeExamples.title}</h2>
            <Separator className="mx-auto w-24 h-1" />
            <p className="text-muted-foreground mt-4">{content.codeExamples.subtitle}</p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {/* Server Examples Column */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <IconServer className="h-5 w-5 text-primary" />
                  {content.codeExamples.server.title}
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-6">
                {content.codeExamples.server.steps.map((step, index) => (
                  <div key={index}>
                    <h3 className="font-medium mb-2">{step.title}</h3>
                    <div className="p-4 rounded-md flex justify-center">
                      <img src={step.image} alt={step.title} className="w-full max-w-md rounded-lg border shadow-lg object-cover" />
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>

            {/* Client Examples Column */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <IconDeviceLaptop className="h-5 w-5 text-primary" />
                  {content.codeExamples.client.title}
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-6">
                {content.codeExamples.client.steps.map((step, index) => (
                  <div key={index}>
                    <h3 className="font-medium mb-2">{step.title}</h3>
                    <div className="p-4 rounded-md flex justify-center">
                      <img src={step.image} alt={step.title} className="w-full max-w-md rounded-lg border shadow-lg object-cover" />
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Design Concepts */}
      <section id="design-concepts" className="py-12">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">{content.designConcepts.title}</h2>
            <Separator className="mx-auto w-24 h-1" />
            <p className="text-muted-foreground mt-4">{content.designConcepts.subtitle}</p>
          </div>

          <div className="grid md:grid-cols-3 gap-8 items-center">
            <div>
              <Badge variant="outline" className="mb-2">{content.designConcepts.simplicity.badge}</Badge>
              <h3 className="text-2xl font-semibold mb-4">{content.designConcepts.simplicity.title}</h3>
              <p className="text-muted-foreground">
                {content.designConcepts.simplicity.description}
              </p>
            </div>
            <div className="flex justify-center">
              <img
                src="/assets/images/rpc/screenshot2.png"
                alt="Design concept"
                className="max-h-80"
              />
            </div>
            <div className="text-right">
              <Badge variant="outline" className="mb-2">{content.designConcepts.intelligence.badge}</Badge>
              <h3 className="text-2xl font-semibold mb-4">{content.designConcepts.intelligence.title}</h3>
              <p className="text-muted-foreground">
                {content.designConcepts.intelligence.description}
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Development Timeline */}
      <section id="development-progress" className="py-12 bg-muted/50">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">{content.timeline.title}</h2>
            <Separator className="mx-auto w-24 h-1" />
            <p className="text-muted-foreground mt-4">{content.timeline.subtitle}</p>
          </div>

          <div className="relative">
            {/* Timeline line */}
            <div className="absolute left-1/2 h-full w-0.5 bg-primary/20 -translate-x-1/2 hidden md:block"></div>

            {/* Timeline items */}
            <div className="space-y-8">
              {content.timeline.items.map((item, index) => (
                <div key={index} className="relative">
                  {/* Dot */}
                  {/*<div*/}
                  {/*  className={`absolute animate-pulse top-1/2 h-4 w-4 rounded-full bg-primary -translate-y-1/2 hidden md:block ${*/}
                  {/*    index % 2 === 0 ? 'right-1/2 translate-x-8 mr-2' : 'left-1/2 -translate-x-8 ml-2'*/}
                  {/*  }`}*/}
                  {/*></div>*/}
                  <div
                    className={`absolute top-1/2 -translate-y-1/2 hidden md:block ${
                      index % 2 === 0 ? 'right-1/2 translate-x-8' : 'left-1/2 -translate-x-8'
                    }`}
                  >
                    <IconCalendarCheck className="h-6 w-6 text-primary" />
                  </div>

                  {/* Content */}
                  <div
                    className={`w-full md:w-1/2 p-4 ${
                      index % 2 === 0
                        ? 'md:ml-auto md:pl-12 md:text-left'
                        : 'md:mr-auto md:pr-12 md:text-right'
                    }`}
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
      <section id="author's-voice" className="py-12 bg-primary text-primary-foreground">
        <div className="container text-center">
          <IconMessageCircle className="mx-auto h-12 w-12 mb-6" />
          <h2 className="text-3xl font-bold mb-6">{content.authorVoice.title}</h2>
          <blockquote className="max-w-3xl mx-auto text-lg italic mb-6">
            "{content.authorVoice.quote}"
          </blockquote>
          <p className="font-medium">{content.authorVoice.signature}</p>
        </div>
      </section>

      {/* Thoughts */}
      <section className="py-12">
        <div className="container">
          <div className="text-center mb-12">
            <h2 className="text-3xl font-bold mb-4">{content.thoughts.title}</h2>
            <Separator className="mx-auto w-24 h-1" />
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {content.thoughts.items.map((thought, index) => (
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
            <h2 className="text-3xl font-bold mb-4">{content.contact.title}</h2>
            <Separator className="mx-auto w-24 h-1" />
          </div>

          {/* Redesigned contact cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-4xl mx-auto">
            {/* Luix Universe Card */}
            <Card className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <div className="w-3 h-3 rounded-full bg-primary animate-pulse"></div>
                  {content.contact.info.title}
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ul className="space-y-3">
                  {content.contact.info.items.map((item, index) => (
                    <li key={index} className="flex items-start gap-3">
                      <div className="shrink-0 mt-1">
                        <IconCircle className="h-3 w-3 text-primary" />
                      </div>
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </CardContent>
            </Card>

            {/* GitHub Resources Card */}
            <Card className="hover:shadow-lg transition-shadow">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <IconBrandGithub className="h-5 w-5" />
                  {content.contact.resources.title}
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ul className="space-y-3">
                  {content.contact.resources.items.map((item, index) => (
                    <li key={index}>
                      <a
                        href={item.url}
                        className="flex items-center gap-3 text-primary hover:underline"
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        <IconExternalLink className="h-4 w-4" />
                        <span>{item.text}</span>
                      </a>
                    </li>
                  ))}
                </ul>
              </CardContent>
            </Card>
          </div>

          {/* Social links centered below cards */}
          <div className="text-center mt-12">
            <div className="flex justify-center gap-6">
              <Button variant="outline" size="icon" asChild>
                <a href="mailto:louis@luixtech.cn" aria-label="Email">
                  <IconMail className="h-5 w-5" />
                </a>
              </Button>
              <Button variant="outline" size="icon" asChild>
                <a href="https://github.com/pm6422" aria-label="GitHub" target="_blank" rel="noopener noreferrer">
                  <IconBrandGithub className="h-5 w-5" />
                </a>
              </Button>
              <Button variant="outline" size="icon" asChild>
                <a href="http://weibo.cn/luix" aria-label="Weibo" target="_blank" rel="noopener noreferrer">
                  <IconBrandWeibo className="h-5 w-5" />
                </a>
              </Button>
            </div>

            <p className="mt-8 text-muted-foreground max-w-2xl mx-auto">
              {content.contact.footer.text}
            </p>
            <p className="mt-4 text-sm text-muted-foreground">
              {content.contact.footer.copyright}
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}
